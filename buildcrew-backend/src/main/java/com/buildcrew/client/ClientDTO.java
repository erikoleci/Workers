package com.buildcrew.client;

public class ClientDTO {
    public String id;
    public String companyName;
    public String contactPerson;
    public String phone;
    public String email;
    public String address;

    public static ClientDTO from(Client c) {
        ClientDTO dto = new ClientDTO();
        dto.id = c.id.toString();
        dto.companyName = c.companyName;
        dto.contactPerson = c.contactPerson;
        dto.phone = c.phone;
        dto.email = c.email;
        dto.address = c.address;
        return dto;
    }
}
