/*  
    Utsav Anantbhat
    CMPT 477 - P3
    301446421
*/

// Q1
method Find(a: array<int>, v: int) returns (index: int) // Method to find an element in the array
    ensures index >= 0 ==> index < a.Length && a[index] == v
    ensures index < 0 ==> forall k :: 0 <= k < a.Length ==> a[k] != v
{
    var i: int := 0;
    while i < a.Length
        invariant 0 <= i <= a.Length
        invariant forall k :: 0 <= k < i ==> a[k] != v
    {
        if a[i] == v
        {
            return i;
        }
        i := i + 1;
    }
    return -1;
}

// Q2
method Sum(n: int) returns (sum: int) // Method to acquire the sum of 10n + 10(n-1) + ... + 10
    requires n > 0
    ensures sum == (5*n) * (n+1)
{
    sum := 0;
    var i: int := n;
    while i > 0
        invariant 0 <= i <= n+1
        invariant sum == ((5*n) * (n+1)) - ((5*(i)) * (i+1))
    {
        var k: int := 0;
        var j: int := i;
        while j > 0
            invariant 0 <= j <= i
            invariant k == 10*(i-j)
        {
            k := k + 10;
            j := j - 1;
        }
        sum := sum + k;
        i := i - 1;
    }
    return sum; // Return the sum
}

// Q3
method ArrayMin(a: array<int>) returns (min: int) // Method to find the minimum value in the array
	requires 0 < a.Length
	ensures forall k :: 0 <= k < a.Length ==> min <= a[k] // min is less than or equal to all elements in the array
	ensures exists k :: 0 <= k < a.Length && min == a[k] // min is equal to some element in the array
{
	var index := 1;
    min := a[0];
	while index < a.Length
		invariant 0 <= index <= a.Length
		invariant forall k :: 0 <= k < index ==> min <= a[k]
        invariant exists k :: 0 <= k < index && min == a[k]
	{
		if a[index] < min { min := a[index]; }
		index := index + 1;
	}
    return min; // Return the min value
}

// Q4
datatype Side = Front | Back // Use datatype for sides, similar to DutchFlag program

predicate Before(c: Side, d: Side)
{
    c == Front || d == Back
}

method SortCoins(a: array<Side>) returns (side: array<Side>) // Method to sort array of coins showing either Front or Back
    modifies a
    ensures forall i, j :: 0 <= i < j < a.Length ==> Before(a[i], a[j]) // Front side before Back side
    ensures multiset(a[..]) == multiset(old(a[..])) // Sorted array is a permutation of the original array
{
    var w, b := 0, a.Length;
    while w < b
        invariant 0 <= w <= b <= a.Length
        invariant forall i :: 0 <= i < w ==> a[i] == Front
        invariant forall i :: b <= i < a.Length ==> a[i] == Back
        invariant multiset(a[..]) == multiset(old(a[..]))
    {
        match a[w]
        case Front =>
            w := w + 1;
        case Back =>
            b := b - 1;
            a[b], a[w] := a[w], a[b];
    }
    return side; // Return the sorted array
}