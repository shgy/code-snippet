
--- Native Lua implementation of filesystem and platform abstractions,
-- using LuaFileSystem, LZLib, MD5 and LuaCurl.
-- module("luarocks.fs.lua")
local fs_lua = {}

local fs = require("luarocks.fs")

local cfg = require("luarocks.cfg")
local dir = require("luarocks.dir")
local util = require("luarocks.util")
local path = require("luarocks.path")

local socket_ok, zip_ok, unzip_ok, lfs_ok, md5_ok, posix_ok, _
local http, ftp, lrzip, luazip, lfs, md5, posix

if cfg.fs_use_modules then
   socket_ok, http = pcall(require, "socket.http")
   _, ftp = pcall(require, "socket.ftp")
   zip_ok, lrzip = pcall(require, "luarocks.tools.zip")
   unzip_ok, luazip = pcall(require, "zip"); _G.zip = nil
   lfs_ok, lfs = pcall(require, "lfs")
   md5_ok, md5 = pcall(require, "md5")
   posix_ok, posix = pcall(require, "posix")
end

local patch = require("luarocks.tools.patch")

local dir_stack = {}

math.randomseed(os.time())

local dir_separator = "/"

--- Quote argument for shell processing.
-- Adds single quotes and escapes.
-- @param arg string: Unquoted argument.
-- @return string: Quoted argument.
function fs_lua.Q(arg)
   assert(type(arg) == "string")

   -- FIXME Unix-specific
   return "'" .. arg:gsub("'", "'\\''") .. "'"
end

--- Test is file/dir is writable.
-- Warning: testing if a file/dir is writable does not guarantee
-- that it will remain writable and therefore it is no replacement
-- for checking the result of subsequent operations.
-- @param file string: filename to test
-- @return boolean: true if file exists, false otherwise.
function fs_lua.is_writable(file)
   assert(file)
   file = dir.normalize(file)
   local result
   if fs.is_dir(file) then
      local file2 = dir.path(file, '.tmpluarockstestwritable')
      local fh = io.open(file2, 'wb')
      result = fh ~= nil
      if fh then fh:close() end
      os.remove(file2)
   else
      local fh = io.open(file, 'r+b')
      result = fh ~= nil
      if fh then fh:close() end
   end
   return result
end

--- Create a temporary directory.
-- @param name string: name pattern to use for avoiding conflicts
-- when creating temporary directory.
-- @return string or (nil, string): name of temporary directory or (nil, error message) on failure.
function fs_lua.make_temp_dir(name)
   assert(type(name) == "string")
   name = dir.normalize(name)

   local temp_dir = (os.getenv("TMP") or "/tmp") .. "/luarocks_" .. name:gsub(dir.separator, "_") .. "-" .. tostring(math.floor(math.random() * 10000))
   local ok, err = fs.make_dir(temp_dir)
   if ok then
      return temp_dir
   else
      return nil, err
   end
end

local function quote_args(command, ...)
   local out = { command }
   for _, arg in ipairs({...}) do
      assert(type(arg) == "string")
      out[#out+1] = fs.Q(arg)
   end
   return table.concat(out, " ")
end

--- Run the given command, quoting its arguments.
-- The command is executed in the current directory in the dir stack.
-- @param command string: The command to be executed. No quoting/escaping
-- is applied.
-- @param ... Strings containing additional arguments, which are quoted.
-- @return boolean: true if command succeeds (status code 0), false
-- otherwise.
function fs_lua.execute(command, ...)
   assert(type(command) == "string")
   return fs.execute_string(quote_args(command, ...))
end

--- Run the given command, quoting its arguments, silencing its output.
-- The command is executed in the current directory in the dir stack.
-- Silencing is omitted if 'verbose' mode is enabled.
-- @param command string: The command to be executed. No quoting/escaping
-- is applied.
-- @param ... Strings containing additional arguments, which will be quoted.
-- @return boolean: true if command succeeds (status code 0), false
-- otherwise.
function fs_lua.execute_quiet(command, ...)
   assert(type(command) == "string")
   if cfg.verbose then -- omit silencing output
      return fs.execute_string(quote_args(command, ...))
   else
      return fs.execute_string(fs.quiet(quote_args(command, ...)))
   end
end

--- Checks if the given tool is available.
-- The tool is executed using a flag, usually just to ask its version.
-- @param tool_cmd string: The command to be used to check the tool's presence (e.g. hg in case of Mercurial)
-- @param tool_name string: The actual name of the tool (e.g. Mercurial)
-- @param arg string: The flag to pass to the tool. '--version' by default.
function fs_lua.is_tool_available(tool_cmd, tool_name, arg)
   assert(type(tool_cmd) == "string")
   assert(type(tool_name) == "string")

   arg = arg or "--version"
   assert(type(arg) == "string")

   if not fs.execute_quiet(fs.Q(tool_cmd), arg) then
      local msg = "'%s' program not found. Make sure %s is installed and is available in your PATH " ..
                  "(or you may want to edit the 'variables.%s' value in file '%s')"
      return nil, msg:format(tool_cmd, tool_name, tool_name:upper(), cfg.which_config().nearest)
   else
      return true
   end
end

--- Check the MD5 checksum for a file.
-- @param file string: The file to be checked.
-- @param md5sum string: The string with the expected MD5 checksum.
-- @return boolean: true if the MD5 checksum for 'file' equals 'md5sum', false + msg if not
-- or if it could not perform the check for any reason.
function fs_lua.check_md5(file, md5sum)
   file = dir.normalize(file)
   local computed, msg = fs.get_md5(file)
   if not computed then
      return false, msg
   end
   if computed:match("^"..md5sum) then
      return true
   else
      return false, "Mismatch MD5 hash for file "..file
   end
end

--- List the contents of a directory.
-- @param at string or nil: directory to list (will be the current
-- directory if none is given).
-- @return table: an array of strings with the filenames representing
-- the contents of a directory.
function fs_lua.list_dir(at)
   local result = {}
   for file in fs.dir(at) do
      result[#result+1] = file
   end
   return result
end

--- Iterate over the contents of a directory.
-- @param at string or nil: directory to list (will be the current
-- directory if none is given).
-- @return function: an iterator function suitable for use with
-- the for statement.
function fs_lua.dir(at)
   if not at then
      at = fs.current_dir()
   end
   at = dir.normalize(at)
   if not fs.is_dir(at) then
      return function() end
   end
   return coroutine.wrap(function() fs.dir_iterator(at) end)
end

---------------------------------------------------------------------
-- LuaFileSystem functions
---------------------------------------------------------------------

if lfs_ok then

--- Run the given command.
-- The command is executed in the current directory in the dir stack.
-- @param cmd string: No quoting/escaping is applied to the command.
-- @return boolean: true if command succeeds (status code 0), false
-- otherwise.
function fs_lua.execute_string(cmd)
   local code = os.execute(cmd)
   return (code == 0 or code == true)
end

--- Obtain current directory.
-- Uses the module's internal dir stack.
-- @return string: the absolute pathname of the current directory.
function fs_lua.current_dir()
   return lfs.currentdir()
end

--- Change the current directory.
-- Uses the module's internal dir stack. This does not have exact
-- semantics of chdir, as it does not handle errors the same way,
-- but works well for our purposes for now.
-- @param d string: The directory to switch to.
function fs_lua.change_dir(d)
   table.insert(dir_stack, lfs.currentdir())
   d = dir.normalize(d)
   return lfs.chdir(d)
end

--- Change directory to root.
-- Allows leaving a directory (e.g. for deleting it) in
-- a crossplatform way.
function fs_lua.change_dir_to_root()
   local current = lfs.currentdir()
   if not current or current == "" then
      return false
   end
   table.insert(dir_stack, current)
   lfs.chdir("/") -- works on Windows too
   return true
end

--- Change working directory to the previous in the dir stack.
-- @return true if a pop ocurred, false if the stack was empty.
function fs_lua.pop_dir()
   local d = table.remove(dir_stack)
   if d then
      lfs.chdir(d)
      return true
   else
      return false
   end
end

--- Create a directory if it does not already exist.
-- If any of the higher levels in the path name do not exist
-- too, they are created as well.
-- @param directory string: pathname of directory to create.
-- @return boolean or (boolean, string): true on success or (false, error message) on failure.
function fs_lua.make_dir(directory)
   assert(type(directory) == "string")
   directory = dir.normalize(directory)
   local path = nil
   if directory:sub(2, 2) == ":" then
     path = directory:sub(1, 2)
     directory = directory:sub(4)
   else
     if directory:match("^/") then
        path = ""
     end
   end
   for d in directory:gmatch("([^"..dir.separator.."]+)"..dir.separator.."*") do
      path = path and path .. dir.separator .. d or d
      local mode = lfs.attributes(path, "mode")
      if not mode then
         local ok, err = lfs.mkdir(path)
         if not ok then
            return false, err
         end
      elseif mode ~= "directory" then
         return false, path.." is not a directory"
      end
   end
   return true
end

--- Remove a directory if it is empty.
-- Does not return errors (for example, if directory is not empty or
-- if already does not exist)
-- @param d string: pathname of directory to remove.
function fs_lua.remove_dir_if_empty(d)
   assert(d)
   d = dir.normalize(d)
   lfs.rmdir(d)
end

--- Remove a directory if it is empty.
-- Does not return errors (for example, if directory is not empty or
-- if already does not exist)
-- @param d string: pathname of directory to remove.
function fs_lua.remove_dir_tree_if_empty(d)
   assert(d)
   d = dir.normalize(d)
   for i=1,10 do
      lfs.rmdir(d)
      d = dir.dir_name(d)
   end
end

--- Copy a file.
-- @param src string: Pathname of source
-- @param dest string: Pathname of destination
-- @param perms string or nil: Permissions for destination file,
-- or nil to use the source filename permissions
-- @return boolean or (boolean, string): true on success, false on failure,
-- plus an error message.
function fs_lua.copy(src, dest, perms)
   assert(sr