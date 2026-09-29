package com.guardianxr.ar;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

public class DashboardActivity extends Activity {
    private final int BG=0xff06101d, CARD=0xff12233a, MUTED=0xff9fb0c8, BLUE=0xff28a8ff, GREEN=0xff38e6ae, ORANGE=0xffff7438, PURPLE=0xffa76cff;
    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    @Override protected void attachBaseContext(Context n){super.attachBaseContext(LocaleUtil.apply(n));}
    GradientDrawable shape(int fill,float radius,int stroke,int strokeColor){GradientDrawable d=new GradientDrawable();d.setColor(fill);d.setCornerRadius(dp(radius));if(stroke>0)d.setStroke(dp(stroke),strokeColor);return d;}
    TextView text(String s,float size,int color,boolean bold){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setTypeface(Typeface.DEFAULT,bold?Typeface.BOLD:Typeface.NORMAL);return v;}
    void addSpace(LinearLayout l,int h){Space s=new Space(this);l.addView(s,new LinearLayout.LayoutParams(1,dp(h)));}
    LinearLayout cardBase(int strokeColor){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(18),dp(17),dp(18),dp(17));c.setBackground(shape(CARD,20,1,strokeColor));return c;}
    TextView pill(String s,int color){TextView v=text(s,12,Color.WHITE,true);v.setGravity(Gravity.CENTER);v.setPadding(dp(12),dp(7),dp(12),dp(7));v.setBackground(shape(color,30,0,0));return v;}
    void choose(boolean first){final String[] a={"English","हिन्दी","Santali (Ol Chiki)"};new AlertDialog.Builder(this).setTitle(R.string.select_language).setCancelable(!first).setItems(a,(d,w)->{LocaleUtil.save(this,w==0?"en":w==1?"hi":"sat");recreate();}).show();}
    @Override
    public void onCreate(Bundle b)
    {
        super.onCreate(b);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        // Keep GuardianXR UI below the system status bar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setStatusBarColor(BG);
            getWindow().setNavigationBarColor(BG);
            getWindow().getDecorView().setSystemUiVisibility(0);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(0);
        }

        if (LocaleUtil.current(this).isEmpty()) {
            choose(true);
            return;
        }

        showDashboard();
    }

    void showDashboard(){
        SharedPreferences p=getSharedPreferences("guardianxr",MODE_PRIVATE);
        int fs=p.getInt("FIRE_score",0), gs=p.getInt("GAS_score",0); boolean fp=p.getBoolean("FIRE_passed",false), gp=p.getBoolean("GAS_passed",false);
        int completed=(fp?1:0)+(gp?1:0), avg=(fs+gs)/2; String cert=p.getString("last_cert","");
        ScrollView sv=new ScrollView(this);sv.setFillViewport(true);LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(20),dp(20),dp(20),dp(30));root.setBackgroundColor(BG);sv.addView(root);

        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);LinearLayout brandWrap=new LinearLayout(this);brandWrap.setGravity(Gravity.CENTER_VERTICAL);
        GuardianLogoView logo=new GuardianLogoView(this);brandWrap.addView(logo,new LinearLayout.LayoutParams(dp(52),dp(52)));
        LinearLayout brandText=new LinearLayout(this);brandText.setOrientation(LinearLayout.VERTICAL);brandText.setPadding(dp(10),0,0,0);TextView brand=text("GuardianXR",29,Color.WHITE,true);brandText.addView(brand);TextView micro=text("INDUSTRIAL AR SAFETY",9,BLUE,true);micro.setLetterSpacing(.12f);brandText.addView(micro);brandWrap.addView(brandText);top.addView(brandWrap,new LinearLayout.LayoutParams(0,-2,1));String lang=LocaleUtil.current(this);TextView lp=pill("🌐  "+(lang.equals("hi")?"हिन्दी":lang.equals("sat")?"Santali":"English"),0xff12365a);lp.setOnClickListener(v->choose(false));top.addView(lp);root.addView(top);
        TextView tagline=text(getString(R.string.dashboard_tagline),12,MUTED,false);tagline.setPadding(0,dp(5),0,0);root.addView(tagline);TextView motto=text(getString(R.string.dashboard_motto),14,0xff8dc9ff,false);motto.setPadding(0,dp(8),0,0);root.addView(motto);addSpace(root,18);

        LinearLayout stats=cardBase(0xff21405f);LinearLayout sr=new LinearLayout(this);sr.setGravity(Gravity.CENTER);stats.addView(sr);addStat(sr,"🎓",String.valueOf(completed),getString(R.string.stat_modules),GREEN,"modules");addStat(sr,"🏅",avg+"%",getString(R.string.stat_avg_score),ORANGE,"scores");addStat(sr,"▣",cert.isEmpty()?"0":"1",getString(R.string.stat_certificate),BLUE,"certificate");root.addView(stats);addSpace(root,18);

        TextView heading=text(getString(R.string.dashboard_welcome),22,Color.WHITE,true);root.addView(heading);TextView help=text(getString(R.string.dashboard_help),14,MUTED,false);help.setPadding(0,dp(5),0,dp(14));root.addView(help);

        LinearLayout fire=trainingCard("🔥",getString(R.string.fire_module_title),getString(R.string.fire_step1),ORANGE,fs);fire.setOnClickListener(v->openTraining("FIRE"));root.addView(fire);addSpace(root,12);
        LinearLayout gas=trainingCard("☁",getString(R.string.gas_module_title),getString(R.string.gas_step1),BLUE,gs);gas.setOnClickListener(v->openTraining("GAS"));root.addView(gas);addSpace(root,16);

        LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);LinearLayout assess=actionCard("☑",getString(R.string.take_assessment),getString(R.string.assessment),PURPLE);assess.setOnClickListener(v->assessmentChooser());row.addView(assess,new LinearLayout.LayoutParams(0,-2,1));Space gap=new Space(this);row.addView(gap,new LinearLayout.LayoutParams(dp(10),1));LinearLayout verify=actionCard("▣",getString(R.string.verify_certificate),getString(R.string.verify_desc),GREEN);verify.setOnClickListener(v->startActivity(new Intent(this,VerifyActivity.class)));row.addView(verify,new LinearLayout.LayoutParams(0,-2,1));root.addView(row);addSpace(root,12);

        LinearLayout admin=wideAction("▥",getString(R.string.admin_dashboard),getString(R.string.admin_desc),ORANGE);admin.setOnClickListener(v->startActivity(new Intent(this,AdminActivity.class)));root.addView(admin);addSpace(root,12);
        LinearLayout language=wideAction("🌐",getString(R.string.change_language),getString(R.string.language_desc),BLUE);language.setOnClickListener(v->choose(false));root.addView(language);addSpace(root,16);

        LinearLayout offline=cardBase(0xff167d68);offline.setOrientation(LinearLayout.HORIZONTAL);offline.setGravity(Gravity.CENTER_VERTICAL);TextView shield=text("✓",24,GREEN,true);offline.addView(shield,new LinearLayout.LayoutParams(dp(42),-2));LinearLayout ot=new LinearLayout(this);ot.setOrientation(LinearLayout.VERTICAL);ot.addView(text(getString(R.string.offline_ready),15,GREEN,true));ot.addView(text(getString(R.string.offline_detail),12,MUTED,false));offline.addView(ot,new LinearLayout.LayoutParams(0,-2,1));root.addView(offline);
        setContentView(sv);
    }

    void addStat(LinearLayout row,String icon,String value,String label,int color,String type){LinearLayout b=new LinearLayout(this);b.setOrientation(LinearLayout.VERTICAL);b.setGravity(Gravity.CENTER);b.setPadding(dp(4),dp(4),dp(4),dp(4));b.setClickable(true);b.setFocusable(true);b.setBackground(shape(0x0012233a,14,0,0));b.addView(text(icon,20,color,false));b.addView(text(value,22,Color.WHITE,true));b.addView(text(label,11,MUTED,false));b.setOnClickListener(v->{Intent i=new Intent(this,RecordsActivity.class);i.putExtra("view",type);startActivity(i);});row.addView(b,new LinearLayout.LayoutParams(0,dp(92),1));}
    LinearLayout trainingCard(String icon,String title,String desc,int accent,int score){LinearLayout c=cardBase(accent);LinearLayout h=new LinearLayout(this);h.setGravity(Gravity.CENTER_VERTICAL);TextView i=text(icon,28,Color.WHITE,false);h.addView(i,new LinearLayout.LayoutParams(dp(48),-2));LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);tx.addView(text(title,18,Color.WHITE,true));tx.addView(text(desc,13,MUTED,false));h.addView(tx,new LinearLayout.LayoutParams(0,-2,1));h.addView(text("›",32,accent,true));c.addView(h);addSpace(c,12);ProgressBar pb=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);pb.setMax(100);pb.setProgress(score);pb.setProgressTintList(android.content.res.ColorStateList.valueOf(accent));c.addView(pb,new LinearLayout.LayoutParams(-1,dp(7)));TextView s=text(score>0?score+"% "+getString(R.string.assessment_score):getString(R.string.training_microcopy),12,score>0?accent:MUTED,false);s.setPadding(0,dp(7),0,0);c.addView(s);return c;}
    LinearLayout actionCard(String icon,String title,String sub,int accent){LinearLayout c=cardBase(accent);c.setGravity(Gravity.CENTER_VERTICAL);c.setMinimumHeight(dp(142));c.addView(text(icon,25,accent,true));TextView t=text(title,15,Color.WHITE,true);t.setPadding(0,dp(7),0,dp(3));t.setMaxLines(3);c.addView(t);TextView st=text(sub,12,MUTED,false);st.setMaxLines(3);c.addView(st);return c;}
    LinearLayout wideAction(String icon,String title,String sub,int accent){LinearLayout c=cardBase(0xff24415e);c.setOrientation(LinearLayout.HORIZONTAL);c.setGravity(Gravity.CENTER_VERTICAL);TextView ic=text(icon,25,accent,true);c.addView(ic,new LinearLayout.LayoutParams(dp(48),-2));LinearLayout tx=new LinearLayout(this);tx.setOrientation(LinearLayout.VERTICAL);tx.addView(text(title,16,Color.WHITE,true));tx.addView(text(sub,12,MUTED,false));c.addView(tx,new LinearLayout.LayoutParams(0,-2,1));c.addView(text("›",28,accent,true));return c;}
    void openTraining(String module){Intent i=new Intent(this,MainActivity.class);i.putExtra("module",module);startActivity(i);}
    void assessmentChooser(){String[] a={getString(R.string.fire_assessment),getString(R.string.gas_assessment)};new AlertDialog.Builder(this).setTitle(R.string.assessment).setItems(a,(d,w)->{Intent i=new Intent(this,AssessmentActivity.class);i.putExtra("module",w==0?"FIRE":"GAS");startActivity(i);}).show();}
    @Override
    protected void onResume() {
        super.onResume();
        if (LocaleUtil.current(this).isEmpty()) return;
        showDashboard();
    }
    static class GuardianLogoView extends View {
        android.graphics.Paint p=new android.graphics.Paint(1);
        android.graphics.Path path=new android.graphics.Path();
        GuardianLogoView(Context c){super(c);}
        protected void onDraw(android.graphics.Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight(),cx=w/2f;path.reset();path.moveTo(cx,w*.05f);path.lineTo(w*.88f,h*.18f);path.lineTo(w*.82f,h*.60f);path.quadTo(cx,h*.95f,w*.18f,h*.60f);path.lineTo(w*.12f,h*.18f);path.close();p.setStyle(android.graphics.Paint.Style.FILL);p.setColor(0xff0b3157);c.drawPath(path,p);p.setStyle(android.graphics.Paint.Style.STROKE);p.setStrokeWidth(Math.max(3,w*.06f));p.setColor(0xff28a8ff);c.drawPath(path,p);p.setStyle(android.graphics.Paint.Style.FILL);p.setColor(0xff38e6ae);c.drawCircle(cx,h*.46f,w*.20f,p);p.setColor(0xff06101d);c.drawRect(w*.30f,h*.45f,w*.70f,h*.57f,p);p.setStyle(android.graphics.Paint.Style.STROKE);p.setStrokeWidth(Math.max(2,w*.045f));c.drawArc(w*.32f,h*.25f,w*.68f,h*.58f,190,160,false,p);}
    }

}
