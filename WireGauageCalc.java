package com.john.android.tsi.utilities;

import android.util.Log;

public class WireGauageCalc {
    private double doubleZgauge = 0.078, zeroGauage = 0.0983, twoGauge = 0.1563, fourGuge = 0.2485,
            sixGauge = 0.3951, eightGauge = 0.6282, tenGauge = 0.9989,twelveGauge = 1.588,
            fourteenGauge = 2.525, sixteenGauge = 4.016,eighteenGauge = 6.385;
    public WireGauageCalc(){}
    public String[] calculateWireGauge(double distance , double resistanceMax, boolean imperial){
        String[] sizeDistance = new String[2];//0 index is gauge, 1 index is max distance
        Log.d("PowerCableActivity", "distance "+distance+" max r "+resistanceMax+" imperial "+imperial);
        //R = pl/A  length is distance on the line and neutral conductor. The value used in the calculations is double the distance from the power source to end device
        //p = 1.76x10^-8
        double mDistance = distance;
        if(imperial){
            mDistance = mDistance/1000;
            if( mDistance*eighteenGauge<=resistanceMax ){
                sizeDistance[0] ="18AWG";
                sizeDistance[1]=(resistanceMax/eighteenGauge)*1000+"";
                return sizeDistance;
            }
            if( mDistance* sixteenGauge <=resistanceMax ){
                sizeDistance[0] ="16AWG";
                sizeDistance[1]=(resistanceMax/sixteenGauge)*1000+"";
                return sizeDistance;
            }
            if( mDistance*fourteenGauge<=resistanceMax ){
                sizeDistance[0] ="14AWG";
                sizeDistance[1]=(resistanceMax/fourteenGauge)*1000+"";
                return sizeDistance;
            }
            if( mDistance*twelveGauge<=resistanceMax ){
                sizeDistance[0] ="12AWG";
                sizeDistance[1]=(resistanceMax/twelveGauge)*1000+"";
                return sizeDistance;
            }
            if( mDistance*tenGauge<=resistanceMax ){//400/1000*0.9989<=0.59 Ohm
                sizeDistance[0] ="10AWG";
                sizeDistance[1]=(resistanceMax/tenGauge)*1000+"";//590ft
                return sizeDistance;
            }
            if( mDistance*eightGauge<=resistanceMax ){
                sizeDistance[0] ="8AWG";
                sizeDistance[1]=(resistanceMax/eightGauge)*1000+"";
                return sizeDistance;
            }
            if( mDistance*sixGauge<=resistanceMax ){
                sizeDistance[0] ="6AWG";
                sizeDistance[1]=(resistanceMax/sixGauge)*1000+"";
                return sizeDistance;
            }
            if( mDistance*fourGuge<=resistanceMax ){
                sizeDistance[0] ="4AWG";
                sizeDistance[1]=(resistanceMax/fourGuge)*1000+"";
                return sizeDistance;
            }
            if( mDistance*twoGauge<=resistanceMax ){
                sizeDistance[0] ="2AWG";
                sizeDistance[1]=(resistanceMax/twoGauge)*1000+"";
                return sizeDistance;
            }
            if( mDistance*zeroGauage<=resistanceMax ){
                sizeDistance[0] ="0AWG";
                sizeDistance[1]=(resistanceMax/zeroGauage)*1000+"";
                return sizeDistance;
            }
            if( mDistance*doubleZgauge<=resistanceMax ){
                sizeDistance[0] ="00AWG";
                sizeDistance[1]=(resistanceMax/doubleZgauge)*1000+"";
                return sizeDistance;
            }
            sizeDistance[0]="Go really big imperial!";
            sizeDistance[1]="Use more than one wire - imperial";
            return sizeDistance;
        }else{
            mDistance = mDistance*0.3048;//convert to meters, 400ft = 122m
            if(  0.0352*mDistance/2.5<=resistanceMax){
                sizeDistance[0]= "2.5mm²";
                sizeDistance[1]=""+(2.5*resistanceMax)/0.0352;//based on the current wire size, determine max distance to provide a margin of error if the actual length is longer
                return sizeDistance;
            }
            if(  0.0352*mDistance/4<=resistanceMax){
                sizeDistance[0]= "4mm²";
                sizeDistance[1]=""+(4*resistanceMax)/0.0352;
                return sizeDistance;
            }
            if(  0.0352*mDistance/10<=resistanceMax){
                sizeDistance[0]= "10mm²";
                sizeDistance[1]=""+(10*resistanceMax)/0.0352;//167m
                return sizeDistance;
            }
            if(  0.0352*mDistance/25<=resistanceMax){
                sizeDistance[0]= "25mm²";
                sizeDistance[1]=""+(25*resistanceMax)/0.0352;//167m
                return sizeDistance;
            }
            if(  0.0352*mDistance/35<=resistanceMax){
                sizeDistance[0]= "35mm²";
                sizeDistance[1]=""+(35*resistanceMax)/0.0352;//167m
                return sizeDistance;
            }
            if(  0.0352*mDistance/50<=resistanceMax) {
                sizeDistance[0]= "50mm²";
                sizeDistance[1]=""+(50*resistanceMax)/0.0352;//167m
                return sizeDistance;
            }
            sizeDistance[0]="Go really big metric!";
            sizeDistance[1]="Use more than one wire - metric";
            return sizeDistance;
        }
    }

}
