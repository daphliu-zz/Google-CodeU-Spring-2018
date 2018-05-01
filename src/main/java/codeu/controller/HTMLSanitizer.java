package codeu.controller;

import java.util.*;

/**
 * Sanitizes incoming HTML code 
 * Takes a whitelist approach 
 */

public class HTMLSanitizer {

	private Set<String> allowed_tags;

	/**
	 * Constructor takes in a variable number of String arguments,
	 * representing the tags that we want to allow
	 * e.g. b, i, em, color etc
	 */
	public HTMLSanitizer(String... args) {
		allowed_tags = new HashSet<String>();

		for (String arg : args) {
			allowed_tags.add(arg);
		}
	}

	/** 
	 * toString method returns a String that lists the allowed tags, separated
	 * by a space.
	 */
	public String toString() {
		String result = "";
		for (String tag : allowed_tags) {
			result += tag + " ";
		}
		return result;
	}

	/** 
	 * Method that takes in HTML code as an input and sanitizes it, based
	 * on the tags that we have decided to allow.
	 */
	public String sanitize(String HTMLInput) {
		String result = "";

		Set<String> open_tags = new HashSet<String>();
		String tag;

		for (int i = 0; i < HTMLInput.length(); i++) {
			if (HTMLInput.charAt(i) == '<') { //start of a tag

				if (HTMLInput.charAt(i+1) == '/') { //closing tag
					int start = i+2;
					int end = start; 
					//space to allow attributes e.g. <form method="get"> should be allowed if the form tag is allowed
					while (end < HTMLInput.length() && HTMLInput.charAt(end) != '>' && HTMLInput.charAt(end) != ' ') {
						end++;
					}
					tag = HTMLInput.substring(start, end);
					System.out.println(tag);
					if (open_tags.contains(tag)) {
						result += "<";
						open_tags.remove(tag);
					} else { //remove tag
						i += end-start+2;
					}

				} else { //opening tag
					//check validity of tag, get start and end values for the tag
					int start = i+1;
					int end = start;
					//space to allow attributes e.g. <form method="get"> should be allowed if the form tag is allowed
					while (end < HTMLInput.length() && HTMLInput.charAt(end) != '>' && HTMLInput.charAt(end) != ' ') {
						end++;
					}
					
					tag = HTMLInput.substring(start, end);
					System.out.println(tag);
					if (allowed_tags.contains(tag))  {
						open_tags.add(tag);
						result += "<";
					} else {
						i += end-start+1;
					}
				}

			} else {
				result += HTMLInput.charAt(i);
			}
		}	
		return result;	
	}
}
