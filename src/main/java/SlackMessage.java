import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Builder(builderClassName = "builder")
@Getter
@Setter
public class SlackMessage implements Serializable {

    private String username;
    private String text;

}

