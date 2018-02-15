package co.edu.uniquindio.campusuq.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.util.NewsAdapter;
import co.edu.uniquindio.campusuq.util.NewsSQLiteController;
import co.edu.uniquindio.campusuq.vo.New;
import co.edu.uniquindio.campusuq.vo.NewCategory;
import co.edu.uniquindio.campusuq.vo.NewRelation;

public class NewsActivity extends MainActivity implements NewsAdapter.OnClickNewListener {

    private RecyclerView mRecyclerView;
    private NewsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<New> news;
    public boolean oldActivity;

    public CallbackManager callbackManager;
    public boolean loggedIn;

    public NewsActivity() {
        this.news = new ArrayList<New>();
        this.oldActivity = false;

        super.setHasNavigationDrawerIcon(false);
    }

    @Override
    public void addContent() {
        super.addContent();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }
            @Override
            public void onCancel() {
                // App code
            }
            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        loggedIn = AccessToken.getCurrentAccessToken() == null;

        super.setBackground(R.drawable.portrait_normal_background, R.drawable.landscape_normal_background);

        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_news);
        View inflated = stub.inflate();

        addItems(getIntent().getStringExtra("CATEGORY"));

    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            boolean found = false;
            for (New mNew : news) {
                if (query.trim().toLowerCase().equals(mNew.getName().toLowerCase()) ||
                        mNew.getName().toLowerCase().contains(query.trim().toLowerCase())) {
                    mLayoutManager.scrollToPosition(news.indexOf(mNew));
                    found = true;
                    break;
                }
            }
            if (!found) {
                Toast.makeText(this, "No se ha encontrado la noticia: "+query, Toast.LENGTH_SHORT).show();
            }
        } else if (mAdapter != null) {
            String category = intent.getStringExtra("CATEGORY");
            this.oldActivity = true;
            addItems(category);
        }
    }

    @Override
    public void onNewClick(int pos, String action) {
        switch(action) {
            case "notice":
                Toast.makeText(this, "To web page: "+news.get(pos).getName(), Toast.LENGTH_SHORT).show();
                break;
            case "more":
                Toast.makeText(this, "New clicked: "+news.get(pos).getName(), Toast.LENGTH_SHORT).show();
                break;
            case "facebook":
                Toast.makeText(this, "Facebook clicked: "+pos, Toast.LENGTH_SHORT).show();
                //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                break;
            case "twitter":
                Toast.makeText(this, "Twitter clicked: "+pos, Toast.LENGTH_SHORT).show();
                break;
            case "whatsapp":
                Toast.makeText(this, "Whatsapp clicked: "+pos, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Undefined: "+pos, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            String fbData = bundle.toString();
            Toast.makeText(this, "Fb OK: "+fbData, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Fb Error: "+data.getExtras().toString(), Toast.LENGTH_SHORT).show();
        }
    }


    public void addItems(String category) {

        New noticia = new New("1", "Claridades sobre el proceso de Matrícula (Actualizado)","https://noticias.uniquindio.edu.co/claridades-sobre-el-proceso-de-matricula-actualizado/",
                "https://noticias.uniquindio.edu.co/wp-content/uploads/2018/02/2.png","Ante las inquietudes que se están presentando referente a la …",
                "02 – Febrero -2018. – Estudiantes de continuidad y estudiantes que se acogieron a los acuerdos 006 y 005.\n" +
                        "\n" +
                        "Los recibos de pago de matrícula ya están disponibles con fecha de pago hasta el 09 de Febrero de 2018.\n" +
                        "\n" +
                        "02 – Febrero -2018. – Como Consultar el Registro Extendido o Semáforo.\n" +
                        "\n" +
                        "Una vez ingrese a SAC en el perfil del estudiante en la parte izquierda podrá encontrar la Actividad ”  Semáforo del estudiante”  para visualizar las materias del plan de estudios e identificar cuales ya curso y cuales están pendientes. Importante tener en cuenta que la información se puede exportar a EXCEL.\n" +
                        "\n" +
                        "31 – Enero -2018. – Registro de Asignaturas estudiantes nuevos.\n" +
                        "\n" +
                        "Los estudiantes nuevos que por alguna razón no asistieron a la programación de registro de Asignaturas, los invitamos para que se acerquen este viernes 2 de febrero del 2018, entre las 8:00 a.m. – 10:00 a.m , al Edificio de Ciencias Básicas piso 4 , salas Multivisuales 1 y 2, para apoyarlos en este proceso.\n" +
                        "26 – Enero -2018. – Registro de Electivas en Modalidad a Distancia.\n" +
                        "\n" +
                        "Se ha realizado desde el pasado martes 23 de Enero del 2018 las equivalencia entre sedes, lo cual permite que la oferta de:  Electivas Complementarias, Obligatorias Institucionales y Obligatorias de Ley  ofrecidas para la modalidad a distancia se puedan visualizar desde el registro de asignaturas del estudiante. Por lo tanto se recomienda que los estudiantes que no hayan realizado el registro de este tipo de materias lo hagan antes de la fecha de cierre de matricula ( Domingo 28 de Enero a las media noche ).\n" +
                        "25 – Enero -2018. – Fecha y Hora de Cierre de Registro de Asignaturas en SAC.\n" +
                        "\n" +
                        "El plazo para cerrar el registro de asignaturas en SAC será hasta el día Domingo 28 de Enero a las 23:59 (Media Noche ), dejando en claro los siguientes aspectos:\n" +
                        "\n" +
                        "1) Los que no han hecho uso del botón cerrar registro, el sistema lo realizará automáticamente en la fecha y hora indicada anteriormente, dejando confirmada la matrícula académica correspondiente.\n" +
                        "\n" +
                        "2) Los estudiantes que aún  tengan asignaturas por registrar por  inconvenientes de cupo, deberán gestionar esta solicitud contactado a su respectivo director del programa y/o secretaria.\n" +
                        "\n" +
                        "3)   Los casos especiales deben ser gestionados con una  solicitud a través del CSU llamando al teléfono (6) 7359300 extensión 300, Enviando un email al correo csu@uniquindio.edu.co, o diligenciando la solicitud desde ECOTIC utilizando el Ícono CSU.\n" +
                        "\n" +
                        "4) Los casos que han sido reportados a través de los programas y del CSU hasta la fecha , se han ido aceleradamente solucionando  para poder atender únicamente  los nuevos.\n" +
                        "\n" +
                        "23 – Enero -2018. – Consejo Académico: Comunicado sobre ampliación del plazo para pago de matrícula.\n" +
                        "\n" +
                        "El Consejo Académico de la Universidad del Quindío aprobó en sesión de este martes 23 de enero de 2018, que el periodo para realizar el pago de matrícula ordinaria se extenderá hasta el próximo miércoles 31 de enero. Solo a partir del miércoles 24 de enero a las 2:00 de la tarde podrán descargar el recibo de pago con la ampliación de fechas. La opción en SAC que deben utilizar es “LIQUIDACIÓN DE MATRÍCULA INDIVIDUAL”, la misma que han venido usando para imprimir o descargar su recibo de pago.\n" +
                        "\n" +
                        "En este sentido, la cancelación de la matrícula extraordinaria se podrá efectuar entre el 1ro y el 5 de febrero del año en curso.\n" +
                        "\n" +
                        "Quienes hayan hecho el pago de carácter extraordinario deberán gestionar el respectivo reembolso ante la Vicerrectoría Administrativa de la institución.\n" +
                        "\n" +
                        "22 – Enero -2018. – Utilizar botón Cerrar Matrícula para culminar su registro de Asignaturas.\n" +
                        "\n" +
                        "Nuevamente hacemos un llamado importante para que los estudiantes, que han realizado la totalidad del registro de asignaturas, utilicen el botón de cerrar matrícula como se explica el vídeotutorial (ver video).\n" +
                        " \n" +
                        "Para la Institución es demasiado importante tener un control del proceso de registro, que permita tomar las acciones y estrategias adecuadas disponiendo de información precisa y así garantizar el éxito del proceso.\n" +
                        " \n" +
                        "Si requiere ayuda o si tiene algun inconveniente con esta consulta por favor comuníquese al CSU: Centro de Servicios al Usuario en el correo csu@uniquindio.edu.co\n" +
                        "o al teléfono 6- 7359300\n" +
                        "19 – Enero -2018. – Se continúa la resolución de casos especiales.\n" +
                        "\n" +
                        "Las personas que han reportado casos especiales ya se tienen datos de los programas y otros recolectados por el CSU,  el lunes a primera hora iniciamos la resolución de los mismo. Sin embargo se tiene previsto estrategias para brindar más apoyo al personal remoto ya que esta semana hubo una alta demanda de atención presencial en CSU y admisiones, lo que dificultó la atención telefónica.\n" +
                        "\n" +
                        "Agradecemos su colaboración y comprensión en la ejecución de este proceso de transición.\n" +
                        "\n" +
                        "19 – Enero -2018. – Se Activa el Botón Confirmar su  Cierre  de  Matrícula.\n" +
                        "\n" +
                        "Desde este Sábado 20 de Enero a partir de las 8:00 am se activa el Botón que permite al estudiante que haya culminado completamente su registro de asignaturas confirmar haciendo uso del mismo como se explica en el vídeo tutorial.\n" +
                        "\n" +
                        "Para la oficina de Admisiones y Registro es muy importante tener el  control y seguimiento al proceso, conocer datos de  quiénes han realizado completamente el registro. Así las cosas los invitamos para que ingresen al registro de asignaturas y cierre el proceso, si y sólo si , esta seguro  de haber registrado todas las asignaturas, de lo contrario omitan hacer uso del botón ya que este  bloquea cualquier modificación.\n" +
                        "\n" +
                        "19 – Enero -2018. – Utiliza los canales de comunicación formales y sugeridos.\n" +
                        "\n" +
                        "El estar utilizando canales o medios de comunicación no formales y estar escribiendo a correos que no son los establecidos para dar soporte a sus solicitudes ha generado mayor desorden al proceso. Solicitamos omitir estar escribiendo a funcionarios que no tienen forma de solucionar sus casos. Los temas de cupos los resuelven en las direcciones de los programas. Los demás casos especiales sólo la oficina de admisiones y el equipo de ingenieros del CSU.\n" +
                        "\n" +
                        "18 – Enero -2018. – Atención de Casos Especiales y Cupos.\n" +
                        "\n" +
                        "Los casos de ampliación de cupos deberán seguir el proceso a través de la dirección del programa. Una vez le sea solucionado su cupo podrá acceder a SAC para realizar  el registro.\n" +
                        " \n" +
                        "Para los demás casos especiales se tendrá como canales de atención : Los Programas ( Que los documentan  y escalan al equipo de Ingenieros ) , La Oficina de Admisiones y el CSU.\n" +
                        "16 – Enero -2018. – Ahora si puedes consultar su turno de Modificaciones al Registro.\n" +
                        "\n" +
                        "Ahora ya pueden consultar su turno para las modificaciones en el registro.\n" +
                        "Nuevamente recomendamos hacerlo por cualquiera de los siguientes medios :\n" +
                        " \n" +
                        "1) Ingresando a SAC\n" +
                        "2) http://micuentadecorreo.uniquindio.edu.co/\n" +
                        "16 – Enero -2018. – Consultas y Modificaciones al Registro de Asignaturas\n" +
                        "\n" +
                        "Con el fin de brindar mayor estabilidad al proceso se ha definido que  el día de hoy martes 15 de Enero del 2018  los Estudiantes puedan Consultar los Turnos de Modificaciones al registro a partir  de las 2:00 p.m. \n" +
                        "La modificación al registro iniciará  desde mañana 17 de Enero del 2018 a partir de las 6:00 a.m y terminará el jueves 18 de Enero del 2018 a las 11:59 p.m.",
                "2 febrero, 2018","Juan David Parra Valencia");

        news.add(noticia);
        New noticia2 = new New("2", "Noticia 2", noticia.getLink(), noticia.getImage(),
                noticia.getSummary(), noticia.getContent(), noticia.getDate(), noticia.getAuthor());
        news.add(noticia2);
        New noticia3 = new New("3", "Noticia 3", noticia.getLink(), noticia.getImage(),
                noticia.getSummary(), noticia.getContent(), noticia.getDate(), noticia.getAuthor());
        news.add(noticia3);

        LoadNewsAsync loadImageAsync = new LoadNewsAsync(this);
        loadImageAsync.execute(category, "new");


    }

    public class LoadNewsAsync extends AsyncTask<String, String, Boolean> {

        private Context context;
        private ProgressDialog pDialog;
        private int scrollTo = 0;

        public LoadNewsAsync(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setTitle(context.getString(R.string.loading_content));
            pDialog.setMessage(context.getString(R.string.please_wait));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {

            Boolean success = true;

            // load news

            NewsSQLiteController dbController = new NewsSQLiteController(NewsActivity.this, 1);
            String validRows = null;

            String events = "";
            ArrayList<NewCategory> categories = dbController.selectCategory(null,
                    NewsSQLiteController.CAMPOS_CATEGORIA[1] + " = ?", new String[]{"Eventos"});
            ArrayList<NewRelation> relations;
            if (categories.size() > 0) {
                relations = dbController.selectRelation(null,
                        NewsSQLiteController.CAMPOS_RELACION[0] + " = ?", new String[]{categories.get(0).getId()});
                for (NewRelation relation : relations) {
                    events += relation.getNewId() + ",";
                }
                events = events.substring(0, events.length() - 1);
                validRows = NewsSQLiteController.CAMPOS_TABLA[0]+" NOT IN ("+events+")";
            }

            if (args[0].equals(getString(R.string.news))) {
                if (args[1].equals("new") && haveNetworkConnection(NewsActivity.this)) {
                    //getNews with date of selecting limiter 1, get categories and relations
                    //foreach obtained select where name equal, if old update, else (empty list) insert
                    //select with limiter news.size() + inserted count
                    int inserted = 0;
                    categories = new ArrayList<>();
                    for (NewCategory category: categories) {
                        ArrayList<NewCategory> oldCategories = dbController.selectCategory("1",
                                NewsSQLiteController.CAMPOS_CATEGORIA[1]+" = ?", new String[]{category.getName()});
                        if (oldCategories.size() == 0) {
                            dbController.insertCategory(category.getName(), category.getLink());
                        }
                    }
                    ArrayList<New> updated = news;
                    for (New mNew : updated) {
                        String imagePath = saveImage(mNew.getImage());
                        if (imagePath != null) {
                            mNew.setImage(imagePath);
                        } else {
                            success = false;
                        }
                        ArrayList<New> olds = dbController.select("1",
                                NewsSQLiteController.CAMPOS_TABLA[0]+" = ?", new String[]{mNew.getId()});
                        if (olds.size() > 0) {
                            dbController.update(mNew.getId(), mNew.getName(), mNew.getLink(), mNew.getImage(),
                                    mNew.getSummary(), mNew.getContent(), mNew.getDate(), mNew.getAuthor());
                        } else {
                            dbController.insert(mNew.getId(), mNew.getName(), mNew.getLink(), mNew.getImage(),
                                    mNew.getSummary(), mNew.getContent(), mNew.getDate(), mNew.getAuthor());
                            //relations of this notice
                            relations = new ArrayList<>();
                            for (NewRelation relation : relations) {
                                dbController.insertRelation(relation.getCategoryId(), relation.getNewId());
                            }
                            inserted += 1;
                        }
                    }
                    news = dbController.select(""+(news.size()+inserted),
                            validRows, null);
                } else {
                    news = dbController.select(""+(news.size()+3),
                            validRows, null);
                    scrollTo = oldActivity ? news.size()-1 : 0;
                }
            } else {

            }

            return success;

        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast.makeText(context, "Error saving images", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            } else {
                if (pDialog != null) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                }

                if (!oldActivity) {
                    mRecyclerView = (RecyclerView) findViewById(R.id.news_recycler_view);
                    mRecyclerView.setHasFixedSize(true);

                    mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    mRecyclerView.setLayoutManager(mLayoutManager);

                    mAdapter = new NewsAdapter(news, NewsActivity.this);
                    mRecyclerView.setAdapter(mAdapter);

                    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                        }

                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                                if (!mRecyclerView.canScrollVertically(-1)) {
                                    if (haveNetworkConnection(context)) {
                                        LoadNewsAsync loadImageAsync = new LoadNewsAsync(context);
                                        loadImageAsync.execute(getIntent().getStringExtra("CATEGORY"), "new");
                                    } else {
                                        Toast.makeText(context, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                                    }
                                } else if (!mRecyclerView.canScrollVertically(1)) {
                                    LoadNewsAsync loadImageAsync = new LoadNewsAsync(context);
                                    loadImageAsync.execute(getIntent().getStringExtra("CATEGORY"), "old");
                                }
                            }
                        }
                    });

                    oldActivity = true;
                } else {
                    mAdapter.setNews(news);
                    mLayoutManager.scrollToPosition(scrollTo);
                }

                Toast.makeText(context, "Images successfully saved", Toast.LENGTH_SHORT).show();
            }
        }

        public String saveImage(String url) {

            String imagePath = null;
            InputStream is = null;

            try {
                URL ImageUrl = new URL(url);

                HttpURLConnection conn = (HttpURLConnection) ImageUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bmImg = BitmapFactory.decodeStream(is, null, options);

                String path = ImageUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/CampusUQ/Media/Images");
                dir.mkdirs();

                File file = new File(dir, idStr);
                if (file.exists()) {
                    file = new File(dir, "new_"+idStr);
                }
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();

                imagePath = file.getPath();
                MediaScannerConnection.scanFile(context,
                        new String[]{imagePath},
                        new String[]{"image/jpeg"}, null);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return imagePath;

        }

    }

}
