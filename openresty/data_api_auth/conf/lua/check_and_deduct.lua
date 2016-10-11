--[[

author: shuaiguangying

desc: check and desc visit count for a period of time

PARAMS:

 KEYS[1] --> ak
 KEYS[2] --> current timestamp

RETURN:
 
  = 0 --> visit count is consumed
  > 0 --> left visit count
  < 0 --> error occurs

--]]
local ak, now = KEYS[1],KEYS[2]

local redis_cleft_key = 'cleft:' .. ak
local redis_info_key = 'info:' .. ak

local info = redis.call('hmget', redis_info_key, 'sdate','edate', 'cunit','climit', 'tleft')
-- if sdate or period or climit not exist return -1
if info[1]==false or info[2]==false or info[3]==false or info[4] == false or info[5] == false then return {-1, "info"}; end

local sdate, edate,now = tonumber(info[1]), tonumber(info[2]), tonumber(now)

if now < sdate or now > edate then
   return {-2, "date"}
end


local cunit, climit, tleft = tonumber(info[3]),tonumber(info[4]), tonumber(info[5])  

if tleft <= 0 then return {-3, "total"}; end

local cleft = redis.call('get', cleft_key)

if cleft and tonumber(cleft) <=0 then return {-4, "cleft"}; end

-- decr total
redis.call('hincrby', info_key, 'tleft', -1)

-- decr climit
if cleft == false then
  local delta = (now - sdate) % cunit 
  if delta == 0 then delta = cunit end
  redis.call('setex',cleft_key, delta , climit-1)
  return {climit-1, 'Reset'}
end

local left = redis.call('decr', cleft_key); 
return {left, 'Decr'}

