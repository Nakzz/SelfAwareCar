package main;

import vision.*;;

public class Check {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		identifytraffic x = new identifytraffic();

		int[] B = x.center();

		int X = B[0];
		int Y = B[1];

		System.out.println("X" + X);
		System.out.println("Y" + Y);

	}
}
