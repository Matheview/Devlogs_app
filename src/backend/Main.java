package backend;

import com.google.gson.Gson;

public class Main {

    public static void main(String[] args) {


        Gson gson = new Gson();


        RequestData rq2 = new RequestData(1,9, "pornhubpremium.com");
        String jsonInputString = gson.toJson(rq2);
        RequestService rs = new RequestService();
        System.out.println("Presylany jSON : " + jsonInputString);
//        ResponseObject ro = rs.requestDeletePermission(jsonInputString);
//        System.out.println(ro);

        ResponseObject ro2 = rs.requestListOfUsers(1);
        System.out.println(ro2);



    }
}
