--[[
0 check if ip is in blacklist
1 check if ak is legal: only contain lower case character or numeric values
2 check if ip match ak: one ak can bind multiple ip address
3 check if uri math ak: one ak can only bind multiple uri
4 check if ak is forbidden or expired or not take effective
5 check if ak total visit count is reached [if need] 
6 check if ak visit count in a period is reached

--]]
local redis = require "resty.redis"
local red = redis:new()
red:set_timeout(1000) -- 1 sec
local redis_host = "127.0.0.1"
local redis_port = 6379
local redis_pass = "redis_passwd"
local redis_lua_check_and_deduct_sha1 = "0d6b542884c7e95e2b946957dc9e67d9bd9bbac6"
local redis_lua_check_and_repay_sha1 =  "f6117f0037320b844efc6f60427d3abea7c8c811"

local akauth = require 'akauth'
local akau = akauth:new(red)

local ok, err = red:connect(redis_host, redis_port)

if not ok then 
   local err_msg = akau:format_err_msg('10010', "failed to connect redis ")
   ngx.say(err_msg); ngx.exit(ngx.HTTP_OK) 
end

-- auth 

local count, err = red:get_reused_times()

if 0 == count then
  local ok, err = red:auth(redis_pass)
  if not ok then
     local err_msg = akau:format_err_msg('10010', "failed to auth redis ")
     ngx.say(err_msg);ngx.exit(ngx.HTTP_OK)
  end
elseif err then
  local err_msg = akau:format_err_msg('10010', "failed to get resued time ")
  ngx.say(err_msg); ngx.exit(ngx.HTTP_OK)
end

-- start business code

local ak, ip, uri = ngx.var.arg_ak, ngx.var.remote_addr, ngx.var.uri

-- check remote addr
local ok, err = akau:check_remote_addr(ip)

if not ok then ngx.say(err); ngx.exit(ngx.HTTP_OK) end

-- check app key
local ok ,err = akau:check_app_key(ak)

if not ok then ngx.say(err); ngx.exit(ngx.HTTP_OK) end

-- check ak ip uri con
local ok ,err = akau:check_consistency(ak,ip,uri)

if not ok then ngx.say(err); ngx.exit(ngx.HTTP_OK) end

-- check and deduct

local ok ,err = akau:check_and_deduct(redis_lua_check_and_deduct_sha1,ak,ngx.time())

if not ok then ngx.say(err); ngx.exit(ngx.HTTP_OK) end

-- request upstream server server
-- 
local res = ngx.location.capture(string.gsub(uri,'api','up_api',1),
     { 
	   method=ngx.HTTP_GET,
	   args = ngx.var.args
     }
)


if 200 == res.status then
  ngx.say(res.body)
  ngx.exit(res.status)
end

if 500 == res.status or 502 == res.status or 503 == res.status or 504 == res.status or 505 == res.status then
   
   local redret, err = akau:check_and_repay(redis_lua_check_and_repay_sha1,ak)
   
   if not redret then 
	local err_msg = akau:format_err_msg('10010','check and repay failed.')
	ngx.say(err_msg); ngx.exit(ngx.HTTP_OK)
   end
   local err_msg = akau:format_err_msg('10010','upstream api server occurs error '..res.status..', retry later.')
   ngx.say(err_msg);ngx.exit(ngx.HTTP_OK)
end

ngx.exit(res.status)

-- end of business code

local ok, err = red:set_keepalive(10000, 100)

if not ok then
  local err_msg = akau:format_err_msg('10010', "failed to set keepalive ")
  ngx.say(err_msg); ngx.exit(ngx.HTTP_OK) 
end

