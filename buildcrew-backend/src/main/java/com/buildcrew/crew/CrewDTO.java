package com.buildcrew.crew;

import java.util.List;

public class CrewDTO {
    public String id;
    public String name;
    public String leaderId;
    public String leaderName;
    public String currentProjectId;
    public String status;
    public List<CrewMemberDTO> members;

    public static class CrewMemberDTO {
        public String workerId;
        public String workerName;
    }
}
