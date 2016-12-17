package org.achacha.webcardgame.dbo;

import org.achacha.webcardgame.base.BaseInitializedTest;
import org.junit.After;

public class TestLogin extends BaseInitializedTest {
//    //TODO: Does not generate anything good
//    @Test
//    public void testToString() throws Exception {
//        Login login = new Login();
//        login.setId(1);
//        login.setEmail("foo@bar");
//        login.setPassword("itsasecret");
//
//        System.out.println("toString="+login.toString());
//        System.out.println("hashCode="+login.hashCode());
//
//        Login login2 = new Login();
//        login2.setId(1);
//        login2.setEmail("foo@bar");
//        login2.setPassword("itsasecret");
//
//        System.out.println("equals="+login.equals(login2));
//    }
//
//    long id;
//
//    //TODO: Does not create new object
//    @Before
//    public void initTestLogin() {
//        System.out.println("Creating data");
//        try (Session session = Factory.getInstance().getSessionFactory().openSession()) {
//            Login login = new Login();
//            login.setEmail("foo@bar");
//            login.setPassword("itsasecret");
//            id = (Long)session.save(login);
//            System.out.println("Inserted new item, id="+id);
//            session.close();
//        }
//    }
//
//    //TODO: init need to insert object for this to work
//    @Test
//    public void test2() {
//        Login login;
//        try (Session session = Factory.getInstance().getSessionFactory().openSession()) {
//            login = session.load(Login.class, id);
//
//            System.out.println("login="+login);
//        }
//    }

    @After
    public void destroyTestLogin() {
//        System.out.println("Removing data");
//        try (Session session = Factory.getInstance().getSessionFactory().openSession()) {
//            Login login = new Login();
//            login.setId(10);
//            session.delete(login);
//        }
    }
}
