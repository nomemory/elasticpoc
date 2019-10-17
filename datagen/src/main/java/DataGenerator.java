import net.andreinc.mockneat.types.enums.DictType;
import net.andreinc.mockneat.unit.text.CSVs;

import java.util.List;
import java.util.Set;

import static net.andreinc.mockneat.unit.address.Cities.cities;
import static net.andreinc.mockneat.unit.objects.From.from;
import static net.andreinc.mockneat.unit.seq.LongSeq.longSeq;
import static net.andreinc.mockneat.unit.seq.Seq.seq;
import static net.andreinc.mockneat.unit.text.CSVs.csvs;
import static net.andreinc.mockneat.unit.time.LocalDates.localDates;
import static net.andreinc.mockneat.unit.types.Ints.ints;
import static net.andreinc.mockneat.unit.user.Emails.emails;
import static net.andreinc.mockneat.unit.user.Names.names;

public class DataGenerator {

    public static void main(String[] args) {

        /** Generate people */
        int numPeople = 100;
        List<Long> personIds = longSeq().start(0).list(numPeople).get();
        List<String> personNames = names().full(40).list(numPeople).get();

        csvs().column(seq(personIds))
              .column(seq(personNames))
              .column(emails())
              .column(ints().range(18,99))
              .separator(",")
              .write("people.csv", numPeople);

        /** Genenerate people - cities */
        int numPeopleCities = 2000;
        List<String> cities = seq(DictType.CITIES_CAPITALS).mapToString().list(100).get();

        csvs().column(from(personIds))
              .column(from(cities))
              .column(localDates().thisYear())
              .separator(",")
              .write("people_cities.csv", numPeopleCities);
    }
}
