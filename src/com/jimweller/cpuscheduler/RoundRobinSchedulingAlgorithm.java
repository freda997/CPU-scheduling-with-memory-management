/** RoundRobinSchedulingAlgorithm.java
 * 
 * A scheduling algorithm that randomly picks the next job to go.
 *
 * @author: Kyle Benson
 * Winter 2013
 *
 */
package com.jimweller.cpuscheduler;

import java.util.*;

public class RoundRobinSchedulingAlgorithm extends BaseSchedulingAlgorithm {

    /** the time slice each process gets */
    private int quantum;
    private int quantumCpy;
    private ArrayList<Process> jobs;
    class RRComparator implements Comparator<Process> {
        public int compare(Process p1, Process p2) {

            return Long.signum(p1.getPID() - p2.getPID());
        }
    }
    RRComparator comparator= new RRComparator();
    RoundRobinSchedulingAlgorithm() {
        // Fill in this method
        /*------------------------------------------------------------*/
        activeJob=null;
        jobs = new ArrayList<Process>();
        quantumCpy=-1;
        /*------------------------------------------------------------*/
    }

    /** Add the new job to the correct queue. */
    public void addJob(Process p) {
        // Remove the next lines to start your implementation
        //throw new UnsupportedOperationException();
        
        // Fill in this method
        /*------------------------------------------------------------*/
        jobs.add(p);
        Collections.sort(jobs,comparator);


        /*------------------------------------------------------------*/
    }

    /** Returns true if the job was present and was removed. */
    public boolean removeJob(Process p) {
        // Remove the next lines to start your implementation
        //throw new UnsupportedOperationException();
        
        // Fill in this method
        /*------------------------------------------------------------*/
        if (p == activeJob)
            activeJob = null;

        return jobs.remove(p);


        /*------------------------------------------------------------*/
    }

    /** Transfer all the jobs in the queue of a SchedulingAlgorithm to another, such as
    when switching to another algorithm in the GUI */
    public void transferJobsTo(SchedulingAlgorithm otherAlg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the value of quantum.
     * 
     * @return Value of quantum.
     */
    public int getQuantum() {
        return quantum;
    }

    /**
     * Set the value of quantum.
     * 
     * @param v
     *            Value to assign to quantum.
     */
    public void setQuantum(int v) {
        this.quantum = v;
    }

    /**
     * Returns the next process that should be run by the CPU, null if none
     * available.
     */
    public Process getNextJob(long currentTime) {
        // Remove the next lines to start your implementation
        //throw new UnsupportedOperationException();
        
        // Fill in this method
        /*------------------------------------------------------------*/
        if (quantumCpy==-1) {
            if (getQuantum()==0){
                setQuantum(10);  //set default to 10
            }
            quantumCpy=getQuantum();
        }
        //System.out.println("current quantum left: "+quantumCpy);
        Process next=null;

        int currIndex=jobs.indexOf(activeJob);
        if (quantumCpy>0 && !isJobFinished()){
            quantumCpy--;
            return activeJob;
        }
        quantumCpy=getQuantum();  //reset quantum number for next process
        if (currIndex== jobs.size()-1){
            next=jobs.get(0);
        }
        else if (jobs.size()>0)
            next=jobs.get(currIndex+1); //get next  job that is larger than current pid in the jobs queue (sorted)
        activeJob=next;
        //System.out.println("current time: "+ currentTime +", active job: "+activeJob.getPID());
        quantumCpy--;
        return activeJob;


        /*------------------------------------------------------------*/
    }

    public String getName() {
        return "Round Robin";
    }
    
}