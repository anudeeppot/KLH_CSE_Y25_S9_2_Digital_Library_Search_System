public class Array_Program {
    
    // Linear Search - searches for element in unsorted array
    public static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i; // Element found at index i
            }
        }
        return -1; // Element not found
    }
    
    // Binary Search - searches for element in sorted array
    public static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                return mid; // Element found at index mid
            } else if (arr[mid] < target) {
                left = mid + 1; // Search right half
            } else {
                right = mid - 1; // Search left half
            }
        }
        return -1; // Element not found
    }
    
    // Helper method to display array
    public static void displayArray(int[] arr) {
        System.out.print("Array: ");
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        // Test Linear Search
        System.out.println("=== Linear Search ===");
        int[] arr1 = {5, 2, 8, 1, 9, 3, 7};
        displayArray(arr1);
        
        int target1 = 8;
        int result1 = linearSearch(arr1, target1);
        if (result1 != -1) {
            System.out.println("Element " + target1 + " found at index: " + result1);
        } else {
            System.out.println("Element " + target1 + " not found in array");
        }
        
        target1 = 10;
        result1 = linearSearch(arr1, target1);
        if (result1 != -1) {
            System.out.println("Element " + target1 + " found at index: " + result1);
        } else {
            System.out.println("Element " + target1 + " not found in array");
        }
        
        // Test Binary Search
        System.out.println("\n=== Binary Search ===");
        int[] arr2 = {1, 2, 3, 5, 7, 8, 9}; // Must be sorted for binary search
        displayArray(arr2);
        
        int target2 = 5;
        int result2 = binarySearch(arr2, target2);
        if (result2 != -1) {
            System.out.println("Element " + target2 + " found at index: " + result2);
        } else {
            System.out.println("Element " + target2 + " not found in array");
        }
        
        target2 = 6;
        result2 = binarySearch(arr2, target2);
        if (result2 != -1) {
            System.out.println("Element " + target2 + " found at index: " + result2);
        } else {
            System.out.println("Element " + target2 + " not found in array");
        }
    }
}
