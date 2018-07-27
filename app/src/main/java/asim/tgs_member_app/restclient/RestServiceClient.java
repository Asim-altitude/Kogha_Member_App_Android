package asim.tgs_member_app.restclient;

import android.content.Context;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by sohaibkhalid on 11/1/16.
 *
 */


public class RestServiceClient  extends Observable {

    @SuppressWarnings("FieldCanBeLocal")
    private String URL = "";
    @SuppressWarnings("FieldCanBeLocal")
    private Object result;
    private String timeStamp;
    @SuppressWarnings("FieldCanBeLocal")
    private SimpleDateFormat dateFormat;
    @SuppressWarnings("FieldCanBeLocal")
    private String previousTimeStamp = "0000-00-00 00:00:00";
    private String request;
    @SuppressWarnings("FieldCanBeLocal")
    private NetworkStateManager networkStateManager;
    private DataSource tpDataSource;



    Context context;



    public void callService(Observer backCommunicator, String params, Class response, String responseType ){

        SQLiteHelper.getInstance(context);

        networkStateManager = new NetworkStateManager();
        tpDataSource = new DataSource(context);

        addObserver(backCommunicator);
        request = "";
        request = URL+params;

        tpDataSource.open();
        result = new Object();


        try
        {
            if(!networkStateManager.isOnline(context) || isCacheValid()){

                result = deserializeObject(tpDataSource.getModel(request));
            }
            else if(networkStateManager.isOnline(context)){

                if(isCacheValid()) {

                    result = deserializeObject(tpDataSource.getModel(request));
                }
                else if(!isCacheValid()){

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

                    byte[] bArray = new byte[0];

                    try{

                        if(responseType!=null && responseType.equals("POST")){
                            //TODO: implement registration mechanism
                        }else{
                            result =  restTemplate.getForObject(request, response);
                        }
                        bArray = serializeObject(result);

                    }catch (Exception e){
                        ErrorModel err = new ErrorModel();
                        err.setStatus(-1);
                        err.setException(e.toString());
                        result = err;
                    }

                    if(result != null ){
                        tpDataSource.add(200, request, timeStamp,bArray);

                    }

                }
            }
            else if(!networkStateManager.isOnline(context) || !isCacheValid()){
                ErrorModel err = new ErrorModel();
                err.setStatus(-2);
                err.setException("Network Error");
                result = err;
                //rvtDataSource.close();
            }
        }

        catch (Exception e)
        {   ErrorModel err = new ErrorModel();
            err.setStatus(-1);
            err.setException("Network Error");
            result = err;
        }

        updateUi(result);

    }


    public void callService(Observer backCommunicator, String params, Class response,
                            String responseType, MultiValueMap<String,String> map){

        SQLiteHelper.getInstance(context);

        networkStateManager = new NetworkStateManager();
        tpDataSource = new DataSource(context);

        addObserver(backCommunicator);
        request = "";
        request = URL+params;

        tpDataSource.open();
        result = new Object();

        try
        {
            if(!networkStateManager.isOnline(context) || isCacheValid()){

                result = deserializeObject(tpDataSource.getModel(request));
            }
            else if(networkStateManager.isOnline(context)){

                if(isCacheValid()) {

                    result = deserializeObject(tpDataSource.getModel(request));
                }
                else if(!isCacheValid()){

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

                    byte[] bArray = new byte[0];

                    try{

                        if(responseType!=null&&responseType.equals("POST")){
                            result = restTemplate.postForObject(request,map,String.class);
                            System.out.print("");
                        }else{
                            result =  restTemplate.getForObject(request, response);
                        }
                        bArray = serializeObject(result);

                    }catch (Exception e){
                        ErrorModel err = new ErrorModel();
                        err.setStatus(-1);
                        err.setException(e.toString());
                        result = err;
                    }

                    if(result != null ){
                        tpDataSource.add(200, request, timeStamp,bArray);

                    }

                }
            }
            else if(!networkStateManager.isOnline(context) || !isCacheValid()){
                ErrorModel err = new ErrorModel();
                err.setStatus(-2);
                err.setException("Network Error");
                result = err;
                //rvtDataSource.close();
            }
        }

        catch (Exception e)
        {   ErrorModel err = new ErrorModel();
            err.setStatus(-1);
            err.setException("Network Error");
            result = err;
        }

        updateUi(result);

    }


    public void callService(Observer backCommunicator, String params, Class response, String responseType,

                            MultiValueMap<String,String> map, boolean cacheResponse){

        networkStateManager = new NetworkStateManager();

        addObserver(backCommunicator);
        request = "";
        request = URL+params;
        result = new Object();

        if (map != null) {
            Log.d("Params", params + map.toString());
        } else {
            Log.d("Params", params);
        }

        try
        {
            if(networkStateManager.isOnline(context)){

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                try{

                    if(responseType!=null&&responseType.equals("POST")){
                        HttpHeaders requestHeaders = new HttpHeaders();
                        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                        OkHttpClientHttpRequestFactory requestFactory = new OkHttpClientHttpRequestFactory();

                        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(map, requestHeaders);

                        restTemplate.setRequestFactory(requestFactory);
                        result = restTemplate.exchange(request, HttpMethod.POST, requestEntity, response).getBody();
                        Log.e("resultPost", String.valueOf(result));
                    }else{
                        result =  restTemplate.getForObject(request, response);
                        Log.e("resultPost", String.valueOf(result));
                    }

                }catch (Exception e){
                    ErrorModel err = new ErrorModel();
                    err.setStatus(-1);
                    err.setException(e.toString());
                    result = err;
                }

            }else {

                ErrorModel err = new ErrorModel();
                err.setStatus(-2);
                err.setException("Network Error");
                result = err;
            }
        }

        catch (Exception e)
        {   ErrorModel err = new ErrorModel();
            err.setStatus(-1);
            err.setException("Network Error");
            result = err;
        }

        updateUi(result);

    }


    public void updateUi(Object result){

        Log.i("in Ui update:...", String.valueOf(result));
        RestServiceClient.this.setChanged();
        RestServiceClient.this.notifyObservers(result);
        RestServiceClient.this.deleteObservers();

    }

    private Object deserializeObject (byte[] byteArray) throws IOException, ClassNotFoundException {

        Object obj;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        ObjectInputStream objectInputS = new ObjectInputStream(byteArrayInputStream);
        obj = objectInputS.readObject();
        objectInputS.close();

        return obj;
    }
    private byte[] serializeObject(Object obj) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try{

            ObjectOutput objectOutput = new ObjectOutputStream(byteArrayOutputStream);
            objectOutput.writeObject((BaseModel)obj);
            objectOutput.close();
            return byteArrayOutputStream.toByteArray();

        }catch(IOException ioe){

            Log.e("serializeObject", "error" + ioe);

            return null;
        }

    }
    private boolean isCacheValid() {

        //TODO Look into the warning and find an alternative
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        timeStamp = dateFormat.format(new Date());
        previousTimeStamp = tpDataSource.getTimeStamp(request);
        long timeDifference = 0;

        try {
            timeDifference = dateFormat.parse(timeStamp).getTime() - dateFormat.parse(previousTimeStamp).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeDifference < 6;
    }


}
