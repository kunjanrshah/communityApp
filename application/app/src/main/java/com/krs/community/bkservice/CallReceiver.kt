/*
package com.krs.community.bkservice

import android.content.Context
import android.widget.Toast
import java.util.*

class CallReceiver : PhonecallReceiver() {

   // private var setLocationDialog: DialogPlus? = null
   // lateinit var mEditMemberListener: EditMemberListener

  //  private lateinit var job_by_update: CompletableJob

   // private lateinit var completableJob: CompletableJob
    //private lateinit var mProfileDetailRepository: ProfileDetailRepository
    override fun onIncomingCallStarted(ctx: Context, number: String, start: Date) {

       // Toast.makeText(ctx, "onIncomingCallStarted $number", Toast.LENGTH_LONG).show()

        //SmartFilterApiData(number,ctx)

        //	showDialogSecond(ctx);



        */
/*val dialog = Dialog(ctx);
        dialog.setContentView(R.layout.truecaller_bottom_sheet);
        dialog.setTitle("Title...");
        dialog.show()*//*

        // val adapter: TruecallerAdapter = TruecallerAdapter(Intent(ctx, SplashActivity::class.java) as Activity)
      // adapter.setTruecallListner(this@CallReceiver)
      */
/* setLocationDialog = DialogPlus.newDialog(ctx)
             //  .setAdapter(adapter)
               .setFooter(R.layout.truecaller_bottom_sheet)
               .setGravity(Gravity.BOTTOM)
               .setCancelable(true)
               .setExpanded(false, 450)
               .setContentBackgroundResource(R.drawable.popup_top_corner)
               .create()
       setLocationDialog?.show()
*//*

//SmartFilterApiData(ctx);
    }

 override fun onIncomingCallEnded(ctx: Context?, number: String?, start: Date?, end: Date?) {
  super.onIncomingCallEnded(ctx, number, start, end)
  Toast.makeText(ctx, "Call dropped$number", Toast.LENGTH_LONG).show()
 }


     */
/*
	private void SmartFilterApiData(Context ctx) {
		ApiServices.login("9427051418","0","1",new Callback<LoginResModel>() {
			@Override
			public void onResponse(Call<LoginResModel> call, retrofit2.Response<LoginResModel> response) {
				if (response.isSuccessful()) {

					if (response.body().getStatus().equalsIgnoreCase("true")) {


					    String success = response.body().getStatus();
					    String getTotal_records = response.body().getTotal_records();
					   *//*



*/
/* Fname = response.body().getMembersResModels().get(0).getFirst_name();
					    Lname = response.body().getMembersResModels().get(0).getLast_name();
					    Pcode = response.body().getMembersResModels().get(0).getPincode();
						Sub_cast_id = response.body().getMembersResModels().get(0).getSub_cast_id();
						Email_address = response.body().getMembersResModels().get(0).getEmail_address();
						Area = response.body().getMembersResModels().get(0).getArea();
						City_id = response.body().getMembersResModels().get(0).getCity_id();
						Gender = response.body().getMembersResModels().get(0).getGender();
						Native_place_id = response.body().getMembersResModels().get(0).getNative_place_id();*//*

*/
/*



					//	showDialogSecond(ctx);
						*//*

*/
/*final Intent intent = new Intent(ctx, MyCustomDialog.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						intent.putExtra("phone_no","9586517742");
						intent.putExtra("Fname",Fname+" "+Lname);
						intent.putExtra("Pcode",Pcode);
						intent.putExtra("Sub_cast_id",Sub_cast_id);
						intent.putExtra("Sub_cast_id",Sub_cast_id);
						intent.putExtra("Email_address",Email_address);
						intent.putExtra("Area",Area);
						intent.putExtra("City_id",City_id);
						intent.putExtra("Gender",Gender);
						intent.putExtra("Native_place_id",Native_place_id);
						ctx.startActivity(intent);
					    Log.e("getTotal_records",""+getTotal_records);
					    Log.e("success",""+success);
					    Log.e("Name---",""+response.body().getMembersResModels().get(0).getFirst_name());
*//*

*/
/*

					} else {

					}
				} else {

				}
			}

			@Override
			public void onFailure(Call<LoginResModel> call, Throwable t) {
				String message = t.getMessage();
				Log.e("error", message);
			}
		});
	}
*//*

*/
/*
	public void showDialogSecond(Context msg){

		final Dialog dialog = new Dialog(MainActivity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(true);
		dialog.setContentView(R.layout.dialog_second);

		TextView text_name = (TextView) dialog.findViewById(R.id.text_name);
		TextView text_Email = (TextView) dialog.findViewById(R.id.text_Email);
		TextView text_gender = (TextView) dialog.findViewById(R.id.text_gender);
		TextView text_City = (TextView) dialog.findViewById(R.id.text_City);
		TextView text_native = (TextView) dialog.findViewById(R.id.text_native);
		TextView text_hdistance = (TextView) dialog.findViewById(R.id.text_hdistance);
		TextView text_Ofcdistance = (TextView) dialog.findViewById(R.id.text_Ofcdistance);
		TextView text_Udistance = (TextView) dialog.findViewById(R.id.text_Udistance);

		text_name.setText(Fname +" "+Lname);
		text_Email.setText(Email_address);
		text_gender.setText(Gender);
		text_City.setText(City_id);
		text_native.setText(Native_place_id);


		dialog.show();

	}
*//*


    */
/*fun SmartFilterApiData(number: String?, ctx: Context) {

        val jsonObject = JSONObject()
        jsonObject.put("" + AppController.mApplication.start, "0")
        jsonObject.put("" + AppController.mApplication.length, "1")
        val jsonObj = JSONObject()
        jsonObj.put("mobile", number)

        jsonObject.put("filter_by", jsonObj)
        val updated = JsonParser().parse(jsonObject.toString()) as JsonObject

        completableJob = Job()

        completableJob.let { thejob ->

            CoroutineScope(Dispatchers.IO + thejob).launch {
                try {
                    val response = mProfileDetailRepository.searchFilter(updated)
                    response.let {
                        withContext(Dispatchers.Main) {
                            mEditMemberListener.getScanResult(response)

                            val intent = Intent(ctx, MyCustomDialog::class.java)
                            ctx.startActivity(intent)

                            Log.e("mEditMemberListener--",""+mEditMemberListener.toString())
                            thejob.complete()
                        }
                        return@launch
                    }
                }catch (e: ApiException){

                }
                thejob.complete()
            }
        }
    }*//*

}*/
