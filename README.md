PdfToc
======

Extract Table of Content (ToC) from PDF file (extract PDF Bookmarks)

## Install

Copy ./bin/PdfToc.jar

## Usage

Get ToC and print in toc.txt

 java -jar PdfToc.jar -i "file.pdf" ["toc.txt"]

Last string is optional - output txt file. If not set, result print on standard output.

Output format:

PageNumber <space> level <space> Title

Where:

'''PageNumber''' - page of pdf file. First page file is 1 (not 0).

'''level''' - level of title. 0 - is root level, 1 - next level, ...

Example

 "Title 1"
   "Title 1.1"
   "Title 1.2"
 "Title 2"

In output file it:

 11 0 Title 1
 23 1 Title 1.1
 37 1 Title 1.2
 49 0 Title 2

