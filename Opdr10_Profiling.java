//To be rewritten using Sets + cleaner methods
//To be added: if a perfect match is found, only display that match + results
//To be added: if not a perfect match is found, display all other books + results

import java.util.*;
import java.io.*;

public class Opdr10_Profiling
{
	public static void main(String[] args) throws IOException
	{
		String fileName = "profiling-data.txt";
		Profiler profiler = new Profiler(fileName);
		Scanner sc = new Scanner(System.in);
		String input = "";

		System.out.print("Enter a name to search: ");
		input = sc.nextLine().trim();
		System.out.println("Looking for " + input);
		profiler.searchForUser(input);

		System.out.print("\nEnter a book tile to search: ");
		input = sc.nextLine().trim();
		System.out.println("Looking for " + input);
		profiler.searchWhoReadBook(input);

		System.out.print("\nEnter a name for who you'd like recommendations: ");
		input = sc.nextLine().trim();
		System.out.println("Looking for " + input);
		//profiler.findAllRecommendations(input);
		profiler.getSingleRecommendation(input);
	}
}

class Profiler
{
	ArrayList<Profile> profiles = new ArrayList<Profile>();

 	public Profiler(String fileName) throws IOException
	{
		readFile(fileName);
	}

	private void readFile(String fileName) throws IOException
	{
		FileReader fileReader = null;
		BufferedReader reader = null;

		try
		{
			fileReader = new FileReader(fileName);
			reader = new BufferedReader(fileReader);

			String line = null;
			while((line = reader.readLine()) != null)
			{
				createProfile(line);
			}
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("Couldn't find the file '" + fileName + "'");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(fileReader != null)
			{
				reader.close();
				fileReader.close();
			}
		}
	}

	private void createProfile(String input)
	{
		String[] inputData = input.split(",");
		Profile currProfile = new Profile();

		for(int i = 0; i < inputData.length; i++)
		{
			if(i == 0)
			{
				currProfile.setName(inputData[i].trim().replace("  "," "));
			}
			else
			{
				currProfile.addBook(inputData[i].trim().replace("  "," "));
			}			
		}
		profiles.add(currProfile);
	}

	public void searchForUser(String name)
	{
		ArrayList<Profile> foundProfiles = findAllMatchingProfiles(name);
		
		if(foundProfiles.size() == 0)
		{
			System.out.println("A profile with the name '" + name + "' could not be found");
		}
		else
		{
			if(foundProfiles.size() > 1)
			{
				System.out.println("\n" + foundProfiles.size() + " profiles containting '" + name + "' were found");
			}
			else
			{
				System.out.println("\n" + foundProfiles.size() + " profile containting '" + name + "' was found");
			}

			for(Profile currProfile: foundProfiles)
			{
				System.out.println("***\nName: " + currProfile.getName());
				System.out.println("Books read: " + currProfile.getBooks() + "\n***");
			}
		}
	}

	private ArrayList<Profile> findAllMatchingProfiles(String name)
	{
		ArrayList<Profile> foundProfiles = new ArrayList<Profile>();
		int counter =0;

		for(Profile currProfile: profiles)
		{
			if(currProfile.getName().toLowerCase().contains(name.toLowerCase()))
			{
				foundProfiles.add(currProfile);
				counter++;
			}		
		}

		return foundProfiles;
	}

	public void searchWhoReadBook(String bookTitle) //maybe change to ALL BOOKS containting the string >> iteration? >>> how to get all booktitles containting string?
	{
		Boolean hasBeenRead = false;

		for(Profile currProfile: profiles)
		{
			if(currProfile.hasReadBook(bookTitle))
			{
				if(!hasBeenRead)
				{
					bookTitle = currProfile.getLastCheckedBookTitle();
					System.out.println("\n***\nThe book '" + bookTitle + "' has been read by: ");
				}
				System.out.println("\t-" + currProfile.getName());
				hasBeenRead = true;
			}
		}
		if(!hasBeenRead)
		{
			System.out.println("No one has read a book called '" + bookTitle + "'");
		}
	}

	public void getSingleRecommendation(String name)
	{
		ArrayList<Profile> foundProfiles = findAllMatchingProfiles(name);

		for(Profile currFoundProfile: foundProfiles)
		{
			ArrayList<String> recommendedBooks = new ArrayList<String>();
			for(Profile currComparedToProfile: profiles)
			{
				if(currComparedToProfile != currFoundProfile)
				{
					ArrayList<String> foundRecommendedBooks =  findRecommendedBooksPerComparedUser(currFoundProfile, currComparedToProfile);
					if(foundRecommendedBooks.size() != 0)
					{
						recommendedBooks = mergeBooklists(recommendedBooks, foundRecommendedBooks);
					}
				}				
			}

			if(recommendedBooks.size() != 0)
			{
				Random r = new Random();
				String randomBook = recommendedBooks.get(r.nextInt(recommendedBooks.size() - 1));
				System.out.println("\n" + currFoundProfile.getName() + " you're recommended to read the book " + randomBook);
			}
			else
			{
				System.out.println("\nNo recommendation could be found for " + currFoundProfile.getName());
			}
			
		}
	}

	public void findAllRecommendations(String name)
	{
		ArrayList<Profile> foundProfiles = findAllMatchingProfiles(name);
		
		
		for(Profile currFoundProfile: foundProfiles)
		{
			ArrayList<String> recommendedBooks = new ArrayList<String>();
			System.out.println("Searching recommendations for " + currFoundProfile.getName());
			for(Profile currComparedToProfile: profiles)
			{
				if(currComparedToProfile != currFoundProfile)
				{
					ArrayList<String> foundRecommendedBooks =  findRecommendedBooksPerComparedUser(currFoundProfile, currComparedToProfile);
					if(foundRecommendedBooks.size() != 0)
					{
						recommendedBooks = mergeBooklists(recommendedBooks, foundRecommendedBooks);
					}
				}				
			}
			printRecommendations(currFoundProfile.getName(), recommendedBooks);
			//System.out.println("\nBook recommendations for " + currFoundProfile.getName() + " are: " + recommendedBooks);
		}
	}

	private void printRecommendations(String name, ArrayList<String> bookList)
	{
		if(bookList.size() != 0)
		{
			System.out.println("\n***\nBook recommendations for " + name + " are: ");
			for(String book : bookList)
			{
				System.out.println("	- " + book);
			}
		}
		else
		{
			System.out.println("\nNo recommendations could be found for " + name); 
		}
		System.out.println("***");
	}

	private ArrayList<String> findRecommendedBooksPerComparedUser(Profile requestedUser, Profile comparedUser)
	{
		ArrayList<String> recommendedBooks = new ArrayList<String>();
		int bookCounter = 0;

		for(String requestedUserBook : requestedUser.getBooks())
		{
			for(String comparedUserBook: comparedUser.getBooks())
			{
				if(requestedUserBook.equals(comparedUserBook))
				{
					bookCounter++;
				}
				else
				{
					if(!(recommendedBooks.contains(comparedUserBook)) && !(requestedUser.getBooks().contains(comparedUserBook)))
					{
						recommendedBooks.add(comparedUserBook);
					}					
				}
			}
		}
		
		if(bookCounter >= 3)
		{
			return recommendedBooks;
		}
		else
		{
			return new ArrayList<String>();
		}
	}

	private ArrayList<String> mergeBooklists(ArrayList<String> mainBooklist, ArrayList<String> toBeAddedBooklist)
	{
		for(String toBeAddedBook: toBeAddedBooklist)
		{
			if(!(mainBooklist.contains(toBeAddedBook)))
			{
				mainBooklist.add(toBeAddedBook);
			}
		}

		return mainBooklist;
	}	
}

class Profile
{
	private String name = "John/Jane Doe";
	private ArrayList<String> booksRead = new ArrayList<String>();
	private String lastCheckedBookTitle = "none";

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public String getLastCheckedBookTitle()
	{
		return lastCheckedBookTitle;
	}

	public void addBook(String bookTitle)
	{
		if(!(booksRead.contains(bookTitle)))
		{
			booksRead.add(bookTitle);
		}
		else
		{
			System.out.println("The book " + bookTitle + " was already added");
		}
		
	}

	public ArrayList<String> getBooks()
	{
		return booksRead;
	}

	public Boolean hasReadBook(String bookTitle)
	{
		for(int i = 0; i < booksRead.size(); i++)
		{
			if(booksRead.get(i).toLowerCase().contains(bookTitle.toLowerCase().trim()))
			{
				lastCheckedBookTitle = booksRead.get(i);
				return true;
			}
		}

		return false;
	}
}	