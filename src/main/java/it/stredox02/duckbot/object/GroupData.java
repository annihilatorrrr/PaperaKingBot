package it.stredox02.duckbot.object;

import it.stredox02.duckbot.permissions.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class GroupData {

    private String chatID;
    private List<PermissionType> permissionList;
    private int resetPerDay;
    private int todayReset;

}
