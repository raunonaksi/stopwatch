package org.yakindu.scr.digitalwatch;
import org.yakindu.scr.ITimer;

public class DigitalwatchStatemachine implements IDigitalwatchStatemachine {

	private final boolean[] timeEvents = new boolean[2];

	private final class SCIButtonsImpl implements SCIButtons {

		private boolean topLeftPressed;

		public void raiseTopLeftPressed() {
			topLeftPressed = true;
		}

		private boolean topLeftReleased;

		public void raiseTopLeftReleased() {
			topLeftReleased = true;
		}

		private boolean topRightPressed;

		public void raiseTopRightPressed() {
			topRightPressed = true;
		}

		private boolean topRightReleased;

		public void raiseTopRightReleased() {
			topRightReleased = true;
		}

		private boolean bottomLeftPressed;

		public void raiseBottomLeftPressed() {
			bottomLeftPressed = true;
		}

		private boolean bottomLeftReleased;

		public void raiseBottomLeftReleased() {
			bottomLeftReleased = true;
		}

		private boolean bottomRightPressed;

		public void raiseBottomRightPressed() {
			bottomRightPressed = true;
		}

		private boolean bottomRightReleased;

		public void raiseBottomRightReleased() {
			bottomRightReleased = true;
		}

		public void clearEvents() {
			topLeftPressed = false;
			topLeftReleased = false;
			topRightPressed = false;
			topRightReleased = false;
			bottomLeftPressed = false;
			bottomLeftReleased = false;
			bottomRightPressed = false;
			bottomRightReleased = false;
		}

	}

	private SCIButtonsImpl sCIButtons;
	private final class SCIDisplayImpl implements SCIDisplay {

		private SCIDisplayOperationCallback operationCallback;

		public void setSCIDisplayOperationCallback(
				SCIDisplayOperationCallback operationCallback) {
			this.operationCallback = operationCallback;
		}

	}

	private SCIDisplayImpl sCIDisplay;
	private final class SCILogicUnitImpl implements SCILogicUnit {

		private SCILogicUnitOperationCallback operationCallback;

		public void setSCILogicUnitOperationCallback(
				SCILogicUnitOperationCallback operationCallback) {
			this.operationCallback = operationCallback;
		}

		private boolean startAlarm;

		public void raiseStartAlarm() {
			startAlarm = true;
		}

		public void clearEvents() {
			startAlarm = false;
		}

	}

	private SCILogicUnitImpl sCILogicUnit;

	public enum State {
		main_region_digitalwatch, main_region_digitalwatch_Time_counting_Counting, main_region_digitalwatch_Display_refreshing_Refreshing, $NullState$
	};

	private final State[] stateVector = new State[2];

	private int nextStateIndex;

	private ITimer timer;

	static {
	}

	public DigitalwatchStatemachine() {

		sCIButtons = new SCIButtonsImpl();
		sCIDisplay = new SCIDisplayImpl();
		sCILogicUnit = new SCILogicUnitImpl();
	}

	public void init() {
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		for (int i = 0; i < 2; i++) {
			stateVector[i] = State.$NullState$;
		}

		clearEvents();
		clearOutEvents();

	}

	public void enter() {
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		entryAction();

		timer.setTimer(this, 0, 1 * 1000, false);

		sCILogicUnit.operationCallback.increaseTimeByOne();

		nextStateIndex = 0;
		stateVector[0] = State.main_region_digitalwatch_Time_counting_Counting;

		timer.setTimer(this, 1, 1 * 1000, false);

		sCIDisplay.operationCallback.refreshTimeDisplay();

		nextStateIndex = 1;
		stateVector[1] = State.main_region_digitalwatch_Display_refreshing_Refreshing;
	}

	public void exit() {
		switch (stateVector[0]) {
			case main_region_digitalwatch_Time_counting_Counting :
				nextStateIndex = 0;
				stateVector[0] = State.$NullState$;

				timer.unsetTimer(this, 0);
				break;

			default :
				break;
		}

		switch (stateVector[1]) {
			case main_region_digitalwatch_Display_refreshing_Refreshing :
				nextStateIndex = 1;
				stateVector[1] = State.$NullState$;

				timer.unsetTimer(this, 1);
				break;

			default :
				break;
		}

		exitAction();
	}

	/**
	 * This method resets the incoming events (time events included).
	 */
	protected void clearEvents() {
		sCIButtons.clearEvents();
		sCILogicUnit.clearEvents();

		for (int i = 0; i < timeEvents.length; i++) {
			timeEvents[i] = false;
		}
	}

	/**
	 * This method resets the outgoing events.
	 */
	protected void clearOutEvents() {
	}

	/**
	 * Returns true if the given state is currently active otherwise false.
	 */
	public boolean isStateActive(State state) {
		switch (state) {
			case main_region_digitalwatch :
				return stateVector[0].ordinal() >= State.main_region_digitalwatch
						.ordinal()
						&& stateVector[0].ordinal() <= State.main_region_digitalwatch_Display_refreshing_Refreshing
								.ordinal();
			case main_region_digitalwatch_Time_counting_Counting :
				return stateVector[0] == State.main_region_digitalwatch_Time_counting_Counting;
			case main_region_digitalwatch_Display_refreshing_Refreshing :
				return stateVector[1] == State.main_region_digitalwatch_Display_refreshing_Refreshing;
			default :
				return false;
		}
	}

	/**
	 * Set the {@link ITimer} for the state machine. It must be set
	 * externally on a timed state machine before a run cycle can be correct
	 * executed.
	 * 
	 * @param timer
	 */
	public void setTimer(ITimer timer) {
		this.timer = timer;
	}

	/**
	 * Returns the currently used timer.
	 * 
	 * @return {@link ITimer}
	 */
	public ITimer getTimer() {
		return timer;
	}

	public void timeElapsed(int eventID) {
		timeEvents[eventID] = true;
	}

	public SCIButtons getSCIButtons() {
		return sCIButtons;
	}
	public SCIDisplay getSCIDisplay() {
		return sCIDisplay;
	}
	public SCILogicUnit getSCILogicUnit() {
		return sCILogicUnit;
	}

	/* Entry action for statechart 'digitalwatch'. */
	private void entryAction() {
	}

	/* Exit action for state 'digitalwatch'. */
	private void exitAction() {
	}

	/* The reactions of state Counting. */
	private void reactMain_region_digitalwatch_Time_counting_Counting() {
		if (timeEvents[0]) {
			nextStateIndex = 0;
			stateVector[0] = State.$NullState$;

			timer.unsetTimer(this, 0);

			timer.setTimer(this, 0, 1 * 1000, false);

			sCILogicUnit.operationCallback.increaseTimeByOne();

			nextStateIndex = 0;
			stateVector[0] = State.main_region_digitalwatch_Time_counting_Counting;
		}
	}

	/* The reactions of state Refreshing. */
	private void reactMain_region_digitalwatch_Display_refreshing_Refreshing() {
		if (timeEvents[1]) {
			nextStateIndex = 1;
			stateVector[1] = State.$NullState$;

			timer.unsetTimer(this, 1);

			timer.setTimer(this, 1, 1 * 1000, false);

			sCIDisplay.operationCallback.refreshTimeDisplay();

			nextStateIndex = 1;
			stateVector[1] = State.main_region_digitalwatch_Display_refreshing_Refreshing;
		}
	}

	public void runCycle() {

		clearOutEvents();

		for (nextStateIndex = 0; nextStateIndex < stateVector.length; nextStateIndex++) {

			switch (stateVector[nextStateIndex]) {
				case main_region_digitalwatch_Time_counting_Counting :
					reactMain_region_digitalwatch_Time_counting_Counting();
					break;
				case main_region_digitalwatch_Display_refreshing_Refreshing :
					reactMain_region_digitalwatch_Display_refreshing_Refreshing();
					break;
				default :
					// $NullState$
			}
		}

		clearEvents();
	}
}
