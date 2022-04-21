package com.example.tasknew;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class DBRemotaHelper {

    private static DBRemotaHelper mInstance;
    private RequestQueue requestQueue;
    private static Context mCtx;
    private DBRemotaHelper(Context Context)
    {
        mCtx = Context;
        requestQueue = getRequestQueue();
    }

    public static  synchronized DBRemotaHelper getInstance (Context context)
    {
        if (mInstance==null)
        {
            mInstance =new DBRemotaHelper(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {

        if (requestQueue==null)
        {
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;

    }

    public<T> void addTorequestque(Request<T> request)
    {
        requestQueue.add(request);
    }
}
