package io.bit.sentiDiary.service;

public interface RedisService {
    public void setValues(String username, String token);
    public String getValues(String username);
    public void delValues(String username);
    public void setValuesWithExp(String name, String token, Long time);
}
