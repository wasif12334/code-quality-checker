@RestController
@RequestMapping("/api")
public class CodeCheckController {

    @PostMapping("/check")
    public String checkCode(@RequestBody String code) {

        if (code.contains("System.out.println")) {
            return " Bad practice: remove debug prints";
        }

        if (code.contains("== null")) {
            return "Use Objects.isNull() or proper null checks";
        }

        return " Code looks clean";
    }
}