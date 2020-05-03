package backend;

import com.google.gson.Gson;

public class Main {

    public static void main(String[] args) {


        Gson gson = new Gson();
        RequestData data = new RequestData("jakub.rucki@gmail.com");
        String jsonInputString = gson.toJson(data);
        RequestService rs = new RequestService();
        ResponseObject ro = rs.requestChangePassword(jsonInputString);

        System.out.println(ro.getMsg());
        System.out.println(ro.isSuccess());
        System.out.println(ro.getLink());



    }
}
