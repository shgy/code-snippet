--[[
0 check if ip is in blacklist
1 check if ak is legal: only contain lower case character or numeric values
2 check if ip match ak: one ak can bind multiple ip address
3 check if uri math ak: one ak can only bind multiple uri
4 check if ak is forbidden or expired or not take effective
5 check if ak total visit count is reached [if need] 
6 check if ak visit count in a period is reached

--]]
ngx.exit(ngx.HTTP_INTERNAL_SERVER_ERROR)
local redis = require "resty.redis"
local red = redis:new()

red:set_timeout(1000) -- 1 sec

local ok, err = red:connect("127.0.0.1", 6379)

if not ok then
  ngx.say("failed to connect: ", err)
  return
end

-- auth 

local count
count , err = red:get_reused_times()
if 0 == count then
  ok, err = red:auth("bbdapi_redis")
  if not ok then
    ngx.say("failed to auth: ", err)
	return
  end
elseif err then
  ngx.say("failed to get reused times: ", err)
  return
end

-- start business code

-- check whiteip

local whiteip = red:sismember('whiteip', ngx.var.remote_addr)
if whiteip == 0 then 
  ngx.say('ip forbidden')
  ngx.exit(ngx.HTTP_FORBIDDEN)
end


local ak = ngx.var.arg_ak

if ak == nil then
  ngx.say('param ak is needed')
  ngx.exit(ngx.HTTP_OK)
end

--local m, err = ngx.re.match(ak,'^[0-9a-z]{32}$')  
--if m == nil then
--  ngx.say('param ak is illegal')
--  ngx.exit(ngx.HTTP_OK)
--end

local exists = red:exists('info:' .. ak)
if exists == 0 then
  ngx.say('param ak not exist')
  ngx.exit(ngx.HTTP_OK)
end
-- check ip address
local ip_ok = red:sismember('ip:' .. ak, ngx.var.remote_addr)
if ip_ok == 0  then
  ngx.say('ip('.. ngx.var.remote_addr ..') is not auth by user.')
  ngx.exit(ngx.HTTP_OK)
end
-- check uri
local uri_ok = red:sismember('api:' .. ak, ngx.var.uri)
if uri_ok == 0 then
  ngx.say('uri('.. ngx.var.uri ..') is not auth by user. ')
  ngx.exit(ngx.HTTP_OK)
end

local res = red:evalsha('0d6b542884c7e95e2b946957dc9e67d9bd9bbac6',2,ak, ngx.time())
if res[1] < 0 then
   ngx.say(res[2])
   ngx.exit(ngx.HTTP_OK)
end

-- end of business code

ok, err = red:set_keepalive(10000, 100)
if not ok then
 ngx.say("failed to set keepalive: ", err)
end
