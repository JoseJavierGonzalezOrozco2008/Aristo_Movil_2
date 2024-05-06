package com.example.aristomovil2.servicio;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.aristomovil2.R;

import java.util.Date;

public class Trabajos extends Worker{
    Context context;

    public Trabajos(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        String chanel=getInputData().getString("CHANEL");//"PEDI001";
        System.out.println("Trabajando..."+chanel+" "+new Date());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, chanel)
                .setSmallIcon(R.drawable.promo_icon)
                .setContentTitle("Ejemplo")
                .setContentText("Tienes un pedido pendiente")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());

        return Result.success();
    }
}
