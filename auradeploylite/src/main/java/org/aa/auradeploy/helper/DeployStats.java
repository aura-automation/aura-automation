/**	   Copyright [2009] [www.apartech.co.uk]


**/
package org.aa.auradeploy.helper;


public class DeployStats {
	
	String operation;
	
	String action;
	
	long startTimeInMillis;
	
	long endTimeInMillis;

	String startTime;
	
	String endTime;

	int earNumber;

	String earName;
	
	String elapsedTime;

	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the startTimeInMillis
	 */
	public long getStartTimeInMillis() {
		return startTimeInMillis;
	}

	/**
	 * @param startTimeInMillis the startTimeInMillis to set
	 */
	public void setStartTimeInMillis(long startTimeInMillis) {
		this.startTimeInMillis = startTimeInMillis;
	}

	/**
	 * @return the endTimeInMillis
	 */
	public long getEndTimeInMillis() {
		return endTimeInMillis;
	}

	/**
	 * @param endTimeInMillis the endTimeInMillis to set
	 */
	public void setEndTimeInMillis(long endTimeInMillis) {
		this.endTimeInMillis = endTimeInMillis;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the earNumber
	 */
	public int getEarNumber() {
		return earNumber;
	}

	/**
	 * @param earNumber the earNumber to set
	 */
	public void setEarNumber(int earNumber) {
		this.earNumber = earNumber;
	}

	/**
	 * @return the earName
	 */
	public String getEarName() {
		return earName;
	}

	/**
	 * @param earName the earName to set
	 */
	public void setEarName(String earName) {
		this.earName = earName;
	}

	/**
	 * @return the elapsedTime
	 */
	public String getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * @param elapsedTime the elapsedTime to set
	 */
	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

}
