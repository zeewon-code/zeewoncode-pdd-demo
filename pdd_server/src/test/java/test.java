import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;



@SpringBootTest(classes = com.zeewoncode.pdd_server.PddServerApplication.class)
public class test {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void PasswordEncoderTest() {
        String password = "Lzx13923514689";
        boolean passwordEncoded = passwordEncoder.matches(password, "$2a$10$MHUq3VfTM1EJjD6WHRx0x.BGkkyLRb1FN7kITdl1N2NNR35ACPlm2");
        if (passwordEncoded) {
            System.out.println("Password is correct!");
        } else {
            System.out.println("Password is incorrect!");
        }
    }
}
