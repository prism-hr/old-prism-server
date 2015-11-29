package uk.co.alumeni.prism.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.type.StringType;
import org.springframework.stereotype.Repository;

@Repository
public class SystemDAO {

    @Inject
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public void clearSchema() {
        List<String> tables = sessionFactory.getCurrentSession().createSQLQuery( //
                "select information_schema.tables.table_name as table_name "
                        + "from information_schema.tables "
                        + "where information_schema.tables.table_schema = 'prism' "
                        + "and information_schema.tables.table_name != 'changelog'") //
                .addScalar("table_name", StringType.INSTANCE) //
                .list();

        sessionFactory.getCurrentSession().createSQLQuery( //
                "set session foreign_key_checks = 0") //
                .executeUpdate();

        tables.forEach(table -> {
            sessionFactory.getCurrentSession().createSQLQuery( //
                    "truncate table " + table)
                    .executeUpdate();
        });

        sessionFactory.getCurrentSession().createSQLQuery( //
                "set session foreign_key_checks = 1") //
                .executeUpdate();
    }

}
