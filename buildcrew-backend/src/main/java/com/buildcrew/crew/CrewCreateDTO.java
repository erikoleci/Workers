package com.buildcrew.crew;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class CrewCreateDTO {

    @NotBlank
    public String name;

    public String leaderId;

    public String currentProjectId;

    public List<String> memberWorkerIds;
}
