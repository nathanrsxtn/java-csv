package dev.nathanrsxtn.csv;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CSVTest {
    public static class Record {
        public static enum Response {
            YES, NO, MAYBE
        }

        public int first;
        public Integer second;
        public AtomicInteger third;
        public Response fourth;

        public Record() {
        }

        private Record(int first, int second, AtomicInteger third, Response fourth) {
            this.first = first;
            this.second = second;
            this.third = third;
            this.fourth = fourth;
        }

        @Override
        public boolean equals(Object obj) {
            Record other = (Record) obj;
            if (first != other.first) return false;
            if (second != other.second) return false;
            if (third != other.third) return false;
            if (fourth != other.fourth) return false;
            return true;
        }

        @Override
        public String toString() {
            return String.format("Record [first=%s, second=%s, third=%s, fourth=%s]", first, second, third, fourth);
        }
    }

    private static final String CSV_HEADER_STRING = "first,second,third,fourth";
    private static final String CSV_RECORD_STRING = "1,2,3,NO";
    private static final Record EXPECTED_RECORD = new Record(1, 2, null, Record.Response.NO);

    private BufferedReader reader;
    private CSV<Record> csv;
    private Stream<Record> stream;
    private Record[] array;

    @BeforeAll
    void setup() {
        reader = new BufferedReader(new StringReader(CSV_HEADER_STRING + "\n" + CSV_RECORD_STRING));
    }

    @Test
    @Order(0)
    void testConstructor() {
        assertDoesNotThrow(() -> csv = new CSV<>(Record::new));
    }

    @Test
    @Order(1)
    void testStreamCreation() throws IOException {
        assertDoesNotThrow(() -> stream = csv.stream(reader));
    }

    @Test
    @Order(2)
    void testStreamTermination() {
        assertDoesNotThrow(() -> array = stream.toArray(Record[]::new));
    }

    @Test
    @Order(3)
    void testRecordContent() {
        assertArrayEquals(new Record[] { EXPECTED_RECORD }, array);
    }
}
