package com.buildcrew.client;

import jakarta.validation.constraints.NotBlank;

public class ClientCreateDTO {

    @NotBlank
    public String companyName;

    public String contactPerson;
    public String phone;
    public String email;
    public String address;
}
