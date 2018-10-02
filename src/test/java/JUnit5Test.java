import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
 
class JUnit5ExampleTest {
 
    @Test
    void justAnExample() {
        System.out.println("This test method should be run");
        assertTrue(true);
    }
    
    @Test
    void anotherExample() {
    	System.out.println("This is a second test method that should be run.");
    	assertTrue(true);
    }
}