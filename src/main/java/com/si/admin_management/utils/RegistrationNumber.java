package com.si.admin_management.utils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class RegistrationNumber {
    public String generate(String entityCode , long listCount ) {
        listCount+=1;
        var registrationNu = entityCode+"-";
        if (listCount < 10){
            registrationNu += "00000"+listCount;
        }else if (listCount < 100){
            registrationNu += "0000"+listCount;
        }else if (listCount < 1000){
            registrationNu += "000"+listCount;
        } else if (listCount < 10000){
            registrationNu += "00"+listCount;
        }else if (listCount < 100000){
            registrationNu += "0"+listCount;
        }else {
            registrationNu += listCount;
        }
        return registrationNu;
    }
}
