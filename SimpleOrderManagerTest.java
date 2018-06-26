import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SimpleOrderManagerTest {
	@InjectMocks 
	SimpleOrderManager simpleOrderManager;
	
	@Mock
    private OrderStore orderStore;
	
	@Mock
    private OrderWriter orderWriter;
	
	
	@Test(expected = InvalidOperationException.class) 
    public void writeAllOrdersNativeCaseTest(){  
		List<Order> emptyList = new ArrayList<Order>();
		// Assume orderStore.getOrders() can return empty list
		when(orderStore.getOrders()).thenReturn(emptyList); 
		// will throw InvalidOperationException what expected
		simpleOrderManager.writeAllOrders();
		// verify the method is called.
        verify(simpleOrderManager).writeAllOrders();
    }  
	
	@Test
    public void writeAllOrdersPositiveCaseTest(){  
		List<Order> orderList = new ArrayList<Order>();
		Order mockOrder1 = mock(Order.class);
		Order mockOrder2 = mock(Order.class);
		orderList.add(mockOrder1);
		orderList.add(mockOrder2);
		Assert.assertEquals(2, orderList.size());  
 
		when(orderStore.getOrders()).thenReturn(orderList); 
		// will not throw any error
		simpleOrderManager.writeAllOrders();
    } 
}
