local _M = {}
_M._VERSION="1.0.0"

local mt = { __index = _M }

function _M.new(self, red)
  return setmetatable({ red = red }, mt)
end


function _M.hello(self)
  local red = self.red
  red:hello()
end


function  _M.format_err_msg(self, code, msg)
   return string.format('{\n "msg": "%s", \n "rsize": 0, \n "total": 0, \n "results": [], \n "err_code": %s \n}',msg,code)

end


-- check whiteip list to see if ip is forbidden

function _M.check_remote_addr(self, remote_addr)
  local red = self.red
  local whiteip, err = red:sismember('whiteip', remote_addr)
  
  if not whiteip then
      return false, self:format_err_msg('10010', err)
  end
  
  if whiteip == 0 then 
    return false, self:format_err_msg('10007', string.format('IP (%s) is forbidden.', remote_addr))
  end
  return true
end


-- check app key is legal

function _M.check_app_key(self, ak)
  
  -- if ak == nil or string.len(ak) ~= 32 or string.match(ak, '^[^%x]+$') ~= nil then
  if ak == nil or string.len(ak) ~= 5 then
    
    local err_msg = self:format_err_msg('10004', string.format('APP Key (%s) is illegal.', ak))
    return false, err_msg
  end
  return true
end 

-- check if appkey uri ip is consistency

function _M.check_consistency(self, ak, ip , uri)
   
   local red = self.red
   local redis_ak_key = 'info:' .. ak
   local redis_ip_key = 'ip:' .. ak
   local redis_uri_key = 'uri:' .. ak
   
   red:init_pipeline()
   red:exists(redis_ak_key)
   red:sismember(redis_ip_key,ip)
   red:sismember(redis_uri_key,uri)
   
   local res, err = red:commit_pipeline()
   
   if not res then
       return false, self:format_err_msg('10010', err)
   end
   
   
   if type(res[1]) == 'table' then
	  local err_msg = self:format_err_msg('10010','redis pipeline command run failed: '.. res[1][2])
	  return false, err_msg
   end
  
   if type(res[2]) == 'table' then
	  local err_msg = self:format_err_msg('10010','redis pipeline command run failed: '.. res[2][2])
	  return false, err_msg
   end
   
   if type(res[3]) == 'table' then
	  local err_msg = self:format_err_msg('10010','redis pipeline command run failed: '.. res[3][2])
	  return false, err_msg
   end
   
   local ak_exist, ip_ismem, uri_ismem = res[1], res[2], res[3]
   
   if ak_exist == 0 then
      local err_msg = self:format_err_msg('10001', string.format('APP Key (%s) is not exists.', ak))
      return false, err_msg
   end
   
   if ip_ismem == 0 then
      local err_msg = self:format_err_msg('10007', string.format('IP (%s) is not auth by user.', ip))
      return false, err_msg
   end

   if uri_ismem == 0 then
      local err_msg = self:format_err_msg('10008', string.format('uri (%s) is not auth by user.', uri))
      return false, err_msg
   end
  return true
end

-- deduct the visit count
--

function _M.check_and_deduct(self, sha1, ak, now)
   local red = self.red
   local res, err = red:evalsha(sha1,2, ak, now)
   
   if not res then
      return false, self:format_err_msg('10010', 'redis evalsha command for deduct failed:' .. err)
   end
   
   if res[1] == -1 then
      return false, self:format_err_msg('10010', 'data in redis is incomplete.')
   end
   
   if res[1] == -2 then
      return false, self:format_err_msg('10005','APP Key is not in effective period.')
   end
   
   if res[1] == -3 then
      return false, self:format_err_msg('10009','request counts have been exhausted.')
   end
   
   if res[1] == -4 then
      return false, self:format_err_msg('10009','request counts in period have been exhausted, retry later.')
   end

   return true
end

function _M.check_and_repay(self, sha1, ak)
  local red = self.red
  local res, err = red:evalsha(sha1, 1, ak)
  
  if not res then
     return false, self:format_err_msg('10010','redis evalsha command for repay falied:' .. err)
  end
  
  return true

end

return _M;
