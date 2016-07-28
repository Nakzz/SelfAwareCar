package main;

import java.util.Arrays;

import vision.*;;

public class Check {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		IdentifyParking x = new IdentifyParking();

		int[] B = x.center();

		int X = (int) B[0];
		int Y = (int) B[1];

		System.out.println("X" + X);
		System.out.println("Y" + Y);

	}
}
