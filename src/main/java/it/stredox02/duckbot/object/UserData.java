package it.stredox02.duckbot.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserData {

    private String chatid;
    private long id;
    private String username;
    private String firstname;
    private String lastname;
    private long time;

}
