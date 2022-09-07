public class Codes100 {
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println("curl \"http://1d3p.wp.codeforces.com/new\" ^\n" +
                    "  -H \"Connection: keep-alive\" ^\n" +
                    "  -H \"Cache-Control: max-age=0\" ^\n" +
                    "  -H \"Upgrade-Insecure-Requests: 1\" ^\n" +
                    "  -H \"Origin: http://1d3p.wp.codeforces.com\" ^\n" +
                    "  -H \"Content-Type: application/x-www-form-urlencoded\" ^\n" +
                    "  -H \"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36\" ^\n" +
                    "  -H \"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\" ^\n" +
                    "  -H \"Referer: http://1d3p.wp.codeforces.com/\" ^\n" +
                    "  -H \"Accept-Language: en-US,en;q=0.9,ru-RU;q=0.8,ru;q=0.7\" ^\n" +
                    "  -H \"Cookie: 70a7c28f3de=qmmvdsz0q18ski7xty; JSESSIONID=27102E97378E2878A77C3FF4803A9930; __utma=71512449.250078693.1631813294.1631869448.1631873621.4; __utmc=71512449; __utmz=71512449.1631873621.4.3.utmcsr=google^|utmccn=(organic)^|utmcmd=organic^|utmctr=(not^%^20provided); __utmb=71512449.1.10.1631873621\" ^\n" +
                    "  --data-raw \"_af=34be50b38beccce4&proof=" + i * i + "&amount=" + i + "&submit=Submit\"");
        }
    }
}
