package elevator;

import org.junit.Assert;
import org.junit.Test;

public class ElevatorSimulationTest
{
	@Test
	public void testIt()
	{
		ElevatorSimulation elevatorSimulation = new ElevatorSimulation();
		Assert.assertEquals("ElevatorSimulation", elevatorSimulation.toString());
	}
}
