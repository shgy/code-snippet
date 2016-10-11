--[[

author: shuaiguangying

desc: check and replay deducted visit cound if error occurs

PARAMS:

 KEYS[1] --> ak

RETURN:
 
  = 0 --> visit count is consumed
  > 0 --> left visit count
  < 0 --> error occurs

--]]
local ak = KEYS[1]

local redis_cleft_key, redis_info_key = 'cleft:' .. ak, 'info:' .. ak

-- decr total
redis.call('hincrby', redis_info_key, 'tleft', 1)

local cleft = redis.call('get', redis_cleft_key)

if cleft ~= false then redis.call('incr', redis_cleft_key); end

return true

