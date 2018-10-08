package br.com.john.combinebrasil.Services;

import android.os.Environment;

import java.io.File;

/**
 * Created by GTAC on 17/10/2016.
 */
public class Constants {
    public static final int colorWhite = 0xffffffff;
    public static final boolean debug = true;

  // public static final String URL = "https://combine-api.herokuapp.com/"; //Ambiente de testes
   public static final String URL = "http://api.combinebrasil.com/"; //Produção

  public static final String AUTHENTICATION =
  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MiwibmFtZSI6IkF0aGxldGUgRm9ybSIsImlhdCI6MTQ4NjQ2NzI3NX0.Dy_2y2yRvYXp1N4_GylIzse-Mt3zX6O35rvddqKRV_g";
  public static final String AUTHORIZATHION = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU5OWFmNDk3YjRhNWI4MDAxMTQ2YmE4NyIsIm5hbWUiOiJKb25hdGhhbiBNYXJxdWVzIiwidGltZSI6MTUwMzMyNzQ5MDg2NiwiaWF0IjoxNTAzMzI3NDkwfQ.qmO_AQA9Oy3Ft-zr1Z48_eE6SJWFPgmMdqkgGn3chcs";
   public static final String URLCep = "https://viacep.com.br/ws/";

    public static final String UPDATE_APP = "update_app";

    public static final String login = "api/login";
    public static final String DATE_LOGIN = "date_login";
    public static final String DATE_UPDATE_TEAM = "date_update_team";

    public static final String API_FORGOT_PASS = "api/forgot";
    public static final String API_ATHLETES = "api/athletes";
    public static final String API_POSITIONS = "api/positions";
    public static final String API_SELECTIVEATHLETES = "api/selectiveAthletes";
    public static final String API_TEAMS = "api/teams";
    public static final String API_TESTTYPES = "api/testTypes";
    public static final String API_SELECTIVE_TESTTYPES = "api/selectiveTestTypes";
    public static final String API_SELECTIVES = "api/selectives";
    public static final String API_TESTS = "api/tests";
    public static final String API_USERS = "api/users";
    public static final String API_SELECTIVE_ATHLETES_SEARCH = "api/selectiveAthletes/search";
    public static final String API_USER_SELECTIVE = "api/selectiveUsers";
    public static final String API_USER_SELECTIVE_SEARCH = "api/selectiveUsers/search";
    public static final String API_RESULTS_SELECTIVE_ATHLETES_SEARCH = "api/resultSelective/search";
    public static final String API_SELECTIVE_TEAM_SEARCH = "api/teams/search";
    public static final String API_SELECTIVE_TESTS_SEARCH = "api/tests/search";
    public static final String URL_INSCRPITION_ATHLETES_SELECTIVE = "http://www.combinebrasil.com.s3-website-us-east-1.amazonaws.com/inscricao/index.html?id=";
    public static final String API_FORGOT_CODE_TEAM = "forgot";

    /*
    **************************CHAMADAS DO VOLLEY***********************************
    **/

    public static final String CALLED_LOGIN = "calledLogin";
    public static final String CALLED_GET_TESTS = "getTests";
    public static final String CALLED_GET_USER = "getUser";
    public static final String CALLED_GET_ATHLETES= "calledGetAthletes";
    public static final String CALLED_GET_POSITIONS= "getPositions";
    public static final String CALLED_GET_SELECTIVE= "getSelective";
    public static final String CALLED_GET_TEAM= "getTeam";
    public static final String CALLED_GET_TESTTYPES= "getTestTypes";
    public static final String CALLED_GET_CEP= "GET_CEP";
    public static final String CALLED_GET_USER_SELECTIVE= "GET_USER_SELECTIVE";
    public static final String CALLED_RESULTS_ATHLETE= "GET_RESULTS_ATHLETE";
    public static final String CALLED_GET_TEST_TYPES= "GET_TEST_TYPES";
    public static final String CALLED_GET_POSITIONS_RESULT= "GET_POSITIONS_RESULT";
    public static final String CALLED_GET_SUBSCRIBERS= "GET_SUBSCRIBERS";
    public static final String CALLED_GET_ATHLETE_SELECTIVE= "METHOD_ATHLETE_SELECTIVE_GET";
    public static final String CALLED_GET_INFORMATIONS_SELECTIVES = "GET_INFO_SELECTIVE";
 public static final String GET_INFO_TEAM = "GET_INFO_TEAM";
    public static final String CALLED_SELECTIVE_ADM = "CALLED_SELECTIVE_ADM";

    public static final String METHOD_EDIT_SELECTIVE = "METHOD_EDIT_SELECTIVE";
    public static final String METHOD_EDIT_TEAM = "METHOD_EDIT_TEAM";

    public static final String CALLED_POST_ATHLETES = "calledPostAthletes";


    public static final String LOGIN_EMAIL = "email";

    public static final String NAME_DATABASE = "Combine.db";
    public static final String TIMER = "Timer";

    /****************************************NAMES ACTIVITYS**********************************/

    public static final String CRONOMETER_ONLY_ONE_ACTIVITY = "CronometerOnlyOneActivity";
    public static final String RESULTS_ONLY_ONE_ACTIVITY = "ResultsOnlyOneActivity";
    public static final String MAIN_ACTIVITY = "MainActivity";
    public static final String TIMER_ACTIVITY = "TimerActivity";
    public static final String LOGIN_CREATEACCOUNTATHLETE = "CreateAccountAthleteActivity";

    //SHARED PREFERENCES
    public static final String
            ID = "Id",
            ENTER_SELECTIVE = "Enter_selective",
            LOGGED="Logged";

    /*
    ***************************DATABASES********************************************
    */

    /*****************************PLAYERS TABLE**********************************/
    public static final String TABLE_ATHLETES = "Athletes";
    public static final String ATHLETES_ID = "_id";
    public static final String ATHLETES_NAME = "name";
    public static final String ATHLETES_BIRTHDAY = "birthday";
    public static final String ATHLETES_EMAIL = "email";
    public static final String ATHLETES_CPF = "cpf";
    public static final String ATHLETES_PHONE = "phoneNumber";
    public static final String ATHLETES_ADDRESS = "address";
    public static final String ATHLETES_DESIRABLE_POSITION = "desirablePosition";
    public static final String ATHLETES_HEIGHT = "height";
    public static final String ATHLETES_WEIGHT = "weight";
    public static final String ATHLETES_CREATEDAT = "createdAt";
    public static final String ATHLETES_UPDATEAT = "updatedAt";
    public static final String ATHLETES_CODE = "code";
    public static final String ATHLETES_SYNC = "sync";
    public static final String ATHLETES_TERMSACCEPTED = "termsAccepted";
    public static final String ATHLETES_IMAGE_URL = "imageUrl";

    /*******************************USER TABLE ***********************************/
    public static final String TABLE_USER = "User";
    public static final String USER_ID = "_id";
    public static final String USER_NAME = "name";
    public static final String USER_PASSWORD = "password";
    public static final String USER_EMAIL = "email";
    public static final String USER_ISADMIN = "isAdmin";
    public static final String USER_CANWRITE = "canWrite";
    public static final String USER_TOKEN = "token";

    /********************************POSITIONS TABLE ******************************/

    public static final String TABLE_POSITIONS = "Positions";
    public static final String POSITIONS_ID = "_id";
    public static final String POSITIONS_NAME = "name";
    public static final String POSITIONS_DESCRIPTIONS = "description";

    /***********************************SELECTIVES ATHLETES TABLE ******************/
    public static final String TABLE_SELECTIVEATHLETES = "SelectiveAthletes";
    public static final String SELECTIVEATHLETES_ID = "_id";
    public static final String SELECTIVEATHLETES_ATHLETE = "athlete";
    public static final String SELECTIVEATHLETES_SELECTIVE = "selective";
    public static final String SELECTIVEATHLETES_PRESENCE = "presence";
    public static final String SELECTIVEATHLETES_INSCRIPTIONNUMBER = "inscriptionNumber";

    /***********************************SELECTIVES ATHLETES TABLE ******************/
    public static final String TABLE_SELECTIVES = "Selectives";
    public static final String SELECTIVES_ID = "_id";
    public static final String SELECTIVES_TITLE = "title";
    public static final String SELECTIVES_TEAM = "team";
    public static final String SELECTIVES_DATE = "date";
    public static final String SELECTIVES_CODESELECTIVE= "codeSelective";
    public static final String SELECTIVES_CANSYNC = "canSync";
    public static final String SELECTIVES_NOTE = "note";
    public static final String SELECTIVES_ADDRESS = "address";
    public static final String SELECTIVES_POSTALCODE = "postalCode";
    public static final String SELECTIVES_NEIGHBORHOOD = "neighborhood";
    public static final String SELECTIVES_CITY = "city";
    public static final String SELECTIVES_STATE = "state";
    public static final String SELECTIVES_STREET = "street";
    public static final String SELECTIVES_ID_ENTER = "id_selective";
    public static final String SELECTIVES_USER = "user";
    public static final String SELECTIVES_PRICE = "price";
    public static final String SELECTIVES_PAY = "pay";

    /****************************** TEAM TABLE***************************************/

    public static final String TABLE_TEAM= "team";
    public static final String TEAM_ID = "_id";
    public static final String TEAM_NAME = "name";
    public static final String TEAM_CITY = "city";
    public static final String TEAM_MODALITY = "modality";
    public static final String TEAM_ADDRESS = "address";
    public static final String TEAM_PRESIDENTNAME= "presidentName";
    public static final String TEAM_EMAIL = "email";
    public static final String TEAM_SOCIAL_LINK = "socialLink";
    public static final String TEAM_URL_IMAGE = "imageUrl";
    public static final String TEAM_CODE = "code";
    public static final String TEAM_USER = "user";

    /****************************** TEAM USERS TABLE***************************************/

    public static final String TABLE_TEAMUSERS = "TeamUsers";
    public static final String TEAMUSERS_ID = "_id";
    public static final String TEAMUSERS_USER = "user";
    public static final String TEAMUSERS_TEAM = "team";

    /****************************** TEAM TYPES TABLE***************************************/

    public static final String TABLE_TESTTYPES = "TestTypes";
    public static final String TESTTYPES_ID = "_id";
    public static final String TESTTYPES_NAME = "name";
    public static final String TESTTYPES_ATTEMPTSLIMIT = "attemptsLimit";
    public static final String TESTTYPES_VISIBLETOREPORT = "visibleToReport";
    public static final String TESTTYPES_DESCRIPTION = "description";
    public static final String TESTTYPES_VALUETYPES= "valueType";
    public static final String TESTTYPES_ICONIMAGEURL= "iconImageUrl";
    public static final String TESTTYPES_TUTORIALIMAGEURL= "tutorialImageUrl";
    public static final String TESTTYPES_REQUIRED_TO_REPORT= "requiredToReport";
    public static final String TESTTYPES_MAIN_TEST= "mainTest";
    public static final String TESTTYPES_SIBLING_TEST_TYPE= "siblingTestType";
    public static final String TESTTYPES_SELECTIVE= "testTypes";

    /************************************TESTS*******************************************/
    public static final String TABLE_TESTS = "Tests";
    public static final String TESTS_ID = "_id";
    public static final String TESTS_TYPE = "type";
    public static final String TESTS_ATHLETE = "athlete";
    public static final String TESTS_SELECTIVE = "selective";
    public static final String TESTS_FIRST_VALUE = "firstValue";
    public static final String TESTS_SECOND_VALUE = "secondValue";
    public static final String TESTS_RATING = "rating";
    public static final String TESTS_WINGSPAN = "wingspan";
    public static final String TESTS_USER = "user";
    public static final String TESTS_SYNC = "sync";
    public static final String TESTS_CANSYNC = "canSync";
    public static final String TESTS_VALUES = "values";

   /*****************************USERXSELECTIVE**************************************/
   public static final String USER_SELECTIVE_USER = "user";
   public static final String USER_SELECTIVE_SELECTIVE = "selective";
   public static final String USER_SELECTIVE_IS_ADMIN = "isSelectiveAdmin";

   public static final String CEP = "cep";
   public static final String STREET = "logradouro";
   public static final String NEIGHBORHOOD = "bairro";
   public static final String CITY = "localidade";
   public static final String STATE = "uf";

       /* public static final String TERMS_TEXT = "<br>Declaro  para os devidos fins de direito  e ciência que:<br><br>" +
                "1. Participarei da seletiva e se caso selecionado, dos treinos e jogos de Futebol Americano e estou em pleno gozo de saúde e em condições de participar da seletiva, treinos e jogos, não apresentando qualquer tipo de impedimento ou restrição à prática e atividades físicas e esportivas.<br><br>" +
                "2.Assumo, todos os riscos e responsabilidade legal das minhas ações envolvidas na participação da seletiva, dos treinos e jogos. Isentando o time, comissão técnica e organizadora, colaboradores e patrocinadores DE TODA E QUALQUER RESPONSABILIDADE por quaisquer danos materiais, morais ou físicos, que porventura venha a sofrer ou provocar.<br><br>" +
                "3.Em caso de lesões do atleta, será acionado o serviço de atendimento médico da rede pública para atender o ocorrido. O atleta ou o seu (sua) acompanhante poderá decidir pelo atendimento, remoção ou transferência para hospitais da rede privada de saúde, eximindo de qualquer responsabilidade, o time, comissão técnica e organizadora, colaboradores e patrocinadores de ônus pelas despesas decorrentes deste atendimento. <br><br>" +
                "5. Concedo, a título universal e de forma irrevogável e irretratável, o direito de usar minha imagem, voz, sons a serem captados na seletiva, treinos e jogos, para usos informativos, promocionais ou publicitários , sem acarretar nenhum ônus ao time, comissão organizadora e aos parceiros. Renunciando o recebimento de qualquer renda que vier a ser auferida com tais direitos em qualquer tempo/data, sendo os direitos reservados ao time e comissão organizadora.<br><br>" +
                "6. Poderá o time solicitar ao atleta, exame médicos periódicos para os treinos e jogos, se caso não for realizado e apresentado, ficará então este exame substituído  por este TERMOS DE RESPONSABILIDADE, eximindo o time e assumindo o atleta suas consequências.<br><br>" +
                "7. Fica eleito o foro da cidade de São Jose dos Campos, preterido qualquer outro, por mais privilegiado que seja, para dirimir quaisquer questões oriundas deste Termo de Responsabilidade.<br><br>";
        */
 public static final String INFO_TEXT = "Você pode customizar sua seletiva, optando pelo número de testes que deseja e de acordo com sua necessidade.\n" +
         "Para obter o relatório aplicado a Metodologia Combine Brasil é necessário selecionar 8 testes dentro das 3 categorias existentes.\n" +
         "\n" +
         "Testes Obrigatórios: Testes obrigatórios para que haja o relatório aplicado à Metodologia Combine Brasil;\n" +
         "\n" +
         "Testes Recomendados: Testes necessários para que haja o relatório completo, podendo optar entre uma opção ou outra dentre os testes irmãos;\n" +
         "\n" +
         "Testes adicionais: Testes que aumentam o conhecimento físico sobre os participantes, porém não são necessários para obter o relatório completo.";
}
