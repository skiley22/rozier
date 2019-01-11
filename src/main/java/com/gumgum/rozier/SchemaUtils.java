package com.gumgum.rozier;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * Utility class which will help you get your test database set up.
 *
 * @author skiley on 2018-12-04
 */
@SuppressWarnings("unused")
public final class SchemaUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaUtils.class);

    private static final List<Pattern> H2_INCOMPATIBLE_FEATURE_REGEX_LIST = asList(
            Pattern.compile("COLLATE[\\s=a-z\\d_]*"),
            Pattern.compile("(?:DEFAULT )?\\QCHARSET\\E?[=\\s]?[\\w]*"),
            Pattern.compile("\\s+\\QON\\E\\s+\\QUPDATE\\E\\s+\\QCURRENT_TIMESTAMP\\E"),
            Pattern.compile("INDEX.*\\n")
    );

    private SchemaUtils() { }

    /**
     * Use this method to execute one or more SQL statements within a String.
     *
     * @param sqlString  String which contains one or many SQL statements. It is assumed that these statements all end with a semicolon.
     *                   This method does not support statements which are separated by "GO".
     */
    public static void executeSqlString(DataSource dataSource, String sqlString) {
        for (String query : sqlString.split("(?<=;)")) {
            try {
                LOGGER.trace("Executing query: {}", query);
                dataSource.getConnection().prepareStatement(query).executeUpdate();
            } catch (Exception e) {
                LOGGER.error(ExceptionUtils.getStackTrace(e));
                return;
            }
        }
    }

    /**
     * These are some MySQL features which are not compatible with H2, as far as I know:
     *
     * COLLATE, CHARSET, ON UPDATE CURRENT_TIMESTAMP, INDEX.
     *
     * @param schemaString  Original schema string
     * @return  Returns a schema string, which is stripped of features incompatible with H2
     */
    public static String getH2CompatibleSchemaString(String schemaString) {
        String h2CompatibleSchemaString = schemaString;

        for (Pattern pattern : H2_INCOMPATIBLE_FEATURE_REGEX_LIST) {
            h2CompatibleSchemaString = pattern.matcher(h2CompatibleSchemaString).replaceAll("");
        }

        return h2CompatibleSchemaString;
    }
}
