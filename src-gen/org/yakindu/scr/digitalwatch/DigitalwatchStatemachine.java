package org.yakindu.scr.digitalwatch;
import org.yakindu.scr.ITimer;

public class DigitalwatchStatemachine implements IDigitalwatchStatemachine {

	private final boolean[] timeEvents = new boolean[5];

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
	private final class SCIStateImpl implements SCIState {

		private boolean timeMode;

		public void raiseTimeMode() {
			timeMode = true;
		}

		private boolean chronoMode;

		public void raiseChronoMode() {
			chronoMode = true;
		}

		public void clearEvents() {
			timeMode = false;
			chronoMode = false;
		}

	}

	private SCIStateImpl sCIState;

	public enum State {
		main_region_digitalwatch, main_region_digitalwatch_Time_counting_Counting, main_region_digitalwatch_Display_refreshing_RefreshingTime, main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_showAlarm, main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm, main_region_digitalwatch_Display_refreshing_RefreshingChrono, main_region_digitalwatch_Display_glowing_GlowOff, main_region_digitalwatch_Display_glowing_GlowOn, main_region_digitalwatch_Display_glowing_GlowDelay, $NullState$
	};

	private final State[] stateVector = new State[3];

	private int nextStateIndex;

	private ITimer timer;

	static {
	}

	public DigitalwatchStatemachine() {

		sCIButtons = new SCIButtonsImpl();
		sCIDisplay = new SCIDisplayImpl();
		sCILogicUnit = new SCILogicUnitImpl();
		sCIState = new SCIStateImpl();
	}

	public void init() {
		if (timer == null) {
			throw new IllegalStateException("timer not set.");
		}
		for (int i = 0; i < 3; i++) {
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

		timer.setTimer(this, 2, 1 * 1000, false);

		sCIDisplay.operationCallback.refreshTimeDisplay();

		sCIDisplay.operationCallback.refreshDateDisplay();

		nextStateIndex = 1;
		stateVector[1] = State.main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm;

		sCIDisplay.operationCallback.unsetIndiglo();

		nextStateIndex = 2;
		stateVector[2] = State.main_region_digitalwatch_Display_glowing_GlowOff;
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
			case main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_showAlarm :
				nextStateIndex = 1;
				stateVector[1] = State.$NullState$;

				timer.unsetTimer(this, 1);
				break;

			case main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm :
				nextStateIndex = 1;
				stateVector[1] = State.$NullState$;

				timer.unsetTimer(this, 2);
				break;

			case main_region_digitalwatch_Display_refreshing_RefreshingChrono :
				nextStateIndex = 1;
				stateVector[1] = State.$NullState$;

				timer.unsetTimer(this, 3);
				break;

			default :
				break;
		}

		switch (stateVector[2]) {
			case main_region_digitalwatch_Display_glowing_GlowOff :
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;
				break;

			case main_region_digitalwatch_Display_glowing_GlowOn :
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;
				break;

			case main_region_digitalwatch_Display_glowing_GlowDelay :
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 4);
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
		sCIState.clearEvents();

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
						&& stateVector[0].ordinal() <= State.main_region_digitalwatch_Display_glowing_GlowDelay
								.ordinal();
			case main_region_digitalwatch_Time_counting_Counting :
				return stateVector[0] == State.main_region_digitalwatch_Time_counting_Counting;
			case main_region_digitalwatch_Display_refreshing_RefreshingTime :
				return stateVector[1].ordinal() >= State.main_region_digitalwatch_Display_refreshing_RefreshingTime
						.ordinal()
						&& stateVector[1].ordinal() <= State.main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm
								.ordinal();
			case main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_showAlarm :
				return stateVector[1] == State.main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_showAlarm;
			case main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm :
				return stateVector[1] == State.main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm;
			case main_region_digitalwatch_Display_refreshing_RefreshingChrono :
				return stateVector[1] == State.main_region_digitalwatch_Display_refreshing_RefreshingChrono;
			case main_region_digitalwatch_Display_glowing_GlowOff :
				return stateVector[2] == State.main_region_digitalwatch_Display_glowing_GlowOff;
			case main_region_digitalwatch_Display_glowing_GlowOn :
				return stateVector[2] == State.main_region_digitalwatch_Display_glowing_GlowOn;
			case main_region_digitalwatch_Display_glowing_GlowDelay :
				return stateVector[2] == State.main_region_digitalwatch_Display_glowing_GlowDelay;
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
	public SCIState getSCIState() {
		return sCIState;
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

	/* The reactions of state showAlarm. */
	private void reactMain_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_showAlarm() {
		if (sCIButtons.topLeftPressed) {
			switch (stateVector[1]) {
				case main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_showAlarm :
					nextStateIndex = 1;
					stateVector[1] = State.$NullState$;

					timer.unsetTimer(this, 1);
					break;

				case main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm :
					nextStateIndex = 1;
					stateVector[1] = State.$NullState$;

					timer.unsetTimer(this, 2);
					break;

				default :
					break;
			}

			timer.setTimer(this, 3, 10, false);

			sCIDisplay.operationCallback.refreshChronoDisplay();

			nextStateIndex = 1;
			stateVector[1] = State.main_region_digitalwatch_Display_refreshing_RefreshingChrono;
		} else {
			if (sCIButtons.bottomLeftPressed) {
				nextStateIndex = 1;
				stateVector[1] = State.$NullState$;

				timer.unsetTimer(this, 1);

				timer.setTimer(this, 2, 1 * 1000, false);

				sCIDisplay.operationCallback.refreshTimeDisplay();

				sCIDisplay.operationCallback.refreshDateDisplay();

				nextStateIndex = 1;
				stateVector[1] = State.main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm;
			} else {
				if (timeEvents[1]) {
					nextStateIndex = 1;
					stateVector[1] = State.$NullState$;

					timer.unsetTimer(this, 1);

					timer.setTimer(this, 1, 1 * 1000, false);

					sCIDisplay.operationCallback.refreshTimeDisplay();

					sCIDisplay.operationCallback.refreshDateDisplay();

					sCIDisplay.operationCallback.refreshAlarmDisplay();

					nextStateIndex = 1;
					stateVector[1] = State.main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_showAlarm;
				}
			}
		}
	}

	/* The reactions of state hideAlarm. */
	private void reactMain_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm() {
		if (sCIButtons.topLeftPressed) {
			switch (stateVector[1]) {
				case main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_showAlarm :
					nextStateIndex = 1;
					stateVector[1] = State.$NullState$;

					timer.unsetTimer(this, 1);
					break;

				case main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm :
					nextStateIndex = 1;
					stateVector[1] = State.$NullState$;

					timer.unsetTimer(this, 2);
					break;

				default :
					break;
			}

			timer.setTimer(this, 3, 10, false);

			sCIDisplay.operationCallback.refreshChronoDisplay();

			nextStateIndex = 1;
			stateVector[1] = State.main_region_digitalwatch_Display_refreshing_RefreshingChrono;
		} else {
			if (sCIButtons.bottomLeftPressed) {
				nextStateIndex = 1;
				stateVector[1] = State.$NullState$;

				timer.unsetTimer(this, 2);

				timer.setTimer(this, 1, 1 * 1000, false);

				sCIDisplay.operationCallback.refreshTimeDisplay();

				sCIDisplay.operationCallback.refreshDateDisplay();

				sCIDisplay.operationCallback.refreshAlarmDisplay();

				nextStateIndex = 1;
				stateVector[1] = State.main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_showAlarm;
			} else {
				if (timeEvents[2]) {
					nextStateIndex = 1;
					stateVector[1] = State.$NullState$;

					timer.unsetTimer(this, 2);

					timer.setTimer(this, 2, 1 * 1000, false);

					sCIDisplay.operationCallback.refreshTimeDisplay();

					sCIDisplay.operationCallback.refreshDateDisplay();

					nextStateIndex = 1;
					stateVector[1] = State.main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm;
				}
			}
		}
	}

	/* The reactions of state RefreshingChrono. */
	private void reactMain_region_digitalwatch_Display_refreshing_RefreshingChrono() {
		if (sCIButtons.topLeftPressed) {
			nextStateIndex = 1;
			stateVector[1] = State.$NullState$;

			timer.unsetTimer(this, 3);

			timer.setTimer(this, 2, 1 * 1000, false);

			sCIDisplay.operationCallback.refreshTimeDisplay();

			sCIDisplay.operationCallback.refreshDateDisplay();

			nextStateIndex = 1;
			stateVector[1] = State.main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm;
		} else {
			if (timeEvents[3]) {
				nextStateIndex = 1;
				stateVector[1] = State.$NullState$;

				timer.unsetTimer(this, 3);

				timer.setTimer(this, 3, 10, false);

				sCIDisplay.operationCallback.refreshChronoDisplay();

				nextStateIndex = 1;
				stateVector[1] = State.main_region_digitalwatch_Display_refreshing_RefreshingChrono;
			}
		}
	}

	/* The reactions of state GlowOff. */
	private void reactMain_region_digitalwatch_Display_glowing_GlowOff() {
		if (sCIButtons.topRightPressed) {
			nextStateIndex = 2;
			stateVector[2] = State.$NullState$;

			sCIDisplay.operationCallback.setIndiglo();

			nextStateIndex = 2;
			stateVector[2] = State.main_region_digitalwatch_Display_glowing_GlowOn;
		}
	}

	/* The reactions of state GlowOn. */
	private void reactMain_region_digitalwatch_Display_glowing_GlowOn() {
		if (sCIButtons.topRightReleased) {
			nextStateIndex = 2;
			stateVector[2] = State.$NullState$;

			timer.setTimer(this, 4, 2 * 1000, false);

			nextStateIndex = 2;
			stateVector[2] = State.main_region_digitalwatch_Display_glowing_GlowDelay;
		}
	}

	/* The reactions of state GlowDelay. */
	private void reactMain_region_digitalwatch_Display_glowing_GlowDelay() {
		if (timeEvents[4]) {
			nextStateIndex = 2;
			stateVector[2] = State.$NullState$;

			timer.unsetTimer(this, 4);

			sCIDisplay.operationCallback.unsetIndiglo();

			nextStateIndex = 2;
			stateVector[2] = State.main_region_digitalwatch_Display_glowing_GlowOff;
		} else {
			if (sCIButtons.topRightPressed) {
				nextStateIndex = 2;
				stateVector[2] = State.$NullState$;

				timer.unsetTimer(this, 4);

				sCIDisplay.operationCallback.setIndiglo();

				nextStateIndex = 2;
				stateVector[2] = State.main_region_digitalwatch_Display_glowing_GlowOn;
			}
		}
	}

	public void runCycle() {

		clearOutEvents();

		for (nextStateIndex = 0; nextStateIndex < stateVector.length; nextStateIndex++) {

			switch (stateVector[nextStateIndex]) {
				case main_region_digitalwatch_Time_counting_Counting :
					reactMain_region_digitalwatch_Time_counting_Counting();
					break;
				case main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_showAlarm :
					reactMain_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_showAlarm();
					break;
				case main_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm :
					reactMain_region_digitalwatch_Display_refreshing_RefreshingTime_timeView_hideAlarm();
					break;
				case main_region_digitalwatch_Display_refreshing_RefreshingChrono :
					reactMain_region_digitalwatch_Display_refreshing_RefreshingChrono();
					break;
				case main_region_digitalwatch_Display_glowing_GlowOff :
					reactMain_region_digitalwatch_Display_glowing_GlowOff();
					break;
				case main_region_digitalwatch_Display_glowing_GlowOn :
					reactMain_region_digitalwatch_Display_glowing_GlowOn();
					break;
				case main_region_digitalwatch_Display_glowing_GlowDelay :
					reactMain_region_digitalwatch_Display_glowing_GlowDelay();
					break;
				default :
					// $NullState$
			}
		}

		clearEvents();
	}
}
