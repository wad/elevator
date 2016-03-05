package elevator;

import org.junit.Assert;
import org.junit.Test;

public class ElevatorSimulationTest
{
	@Test
	public void testConstructor()
	{
		// todo: make the test not rely on this level of transparency (too brittle)
		ElevatorSimulation elevatorSimulation = new ElevatorSimulation(3, 10);
		Assert.assertEquals(3, elevatorSimulation.elevatorController.elevators.size());
		Assert.assertEquals(10, elevatorSimulation.elevatorController.elevators.get(0).numFloors);
	}

	// todo: yeah, this is where you can write tests...
}
