package com.project.demo.funcional;

public class MissionTestData {

    public static String validMissionUpdate() {
        return """
        {
          "createdBy": { "id": 1 },
          "createdAt": "2025-04-10T12:00:00Z",
          "startDate": "2025-04-11T08:00:00Z",
          "endDate": "2025-04-15T08:00:00Z",
          "isDaily": true,
          "isActive": true,
          "objective": {
            "id": 1,
            "ammountSuccesses": 6,
            "scoreCondition": 200,
            "objectiveText": "Complete 6 matches with a score of at least 200"
          },
          "experience": 3000,
          "gameType": { "id": 2 }
        }
        """;
    }

    public static String invalidMissionUpdate() {
        return """
        {
          "createdBy": { "id": 1 },
          "objective": {
            "id": 99999
          },
          "gameType": { "id": 99999 }
        }
        """;
    }
}