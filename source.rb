class Word
  include Comparable
  attr_reader :text

  def initialize(text)
    @text = text
  end

  # generates a documentation string representing its receiver as a Ruby object,
  # including its class and any provided text.
  # 
  # @returns [String] a string representation of the object in the format `#<Class
  # Name> #{text>`.
  def inspect
    "#<#{self.class} #{text}>"
  end

  def ==(other)
    other.is_a?(Word) && text == other.text
  end

  def <=>(other)
    text <=> other.text if other.is_a?(Word)
  end

  # matches whether a given string starts with only punctuation symbols.
  # 
  # @returns [Boolean] a boolean value indicating whether the given string contains
  # only punctuation characters.
  def punctuation?
    text.match?(/^[[:punct:]]+$/)
  end

  # creates a new `Word` object from the provided `text`, with each letter capitalized.
  # 
  # @returns [Object] a new `Word` object containing the capitalized version of the
  # given text.
  def capitalize
    Word.new(text.capitalize)
  end

  # creates a new instance of `Word` object from a given string in lowercase.
  # 
  # @returns [instance of `Word`.] a new `Word` object with the lowercased text.
  # 
  # 		- Type: `Word` class object
  # 		+ Method: `text` accessor property
  # 			- Return type: string
  # 			- Description: The lowercase version of the given word.
  # 		- Instance variables: None
  # 
  # 	Therefore, when calling `downcase(word)` and providing a string as input, the
  # function will return a `Word` object with the lowercase version of the given word
  # as its `text` property.
  def downcase
    Word.new(text.downcase)
  end

  # creates a new `Word` object containing the provided text in uppercase letters.
  # 
  # @returns [Object] a new `Word` object containing the uppercased version of the
  # input text.
  def upcase
    Word.new(text.upcase)
  end

  # creates a new `Word` instance by reversing a string using the `reverse` method,
  # and then returns that instance.
  # 
  # @returns [Object] a new `Word` object containing the reversed text.
  def reverse
    Word.new(text.reverse)
  end
end

word1 = Word.new("hello")
word2 = Word.new("world")
word3 = Word.new("!")

puts "word1: #{word1.inspect}"
puts "word2: #{word2.inspect}"
puts "word3: #{word3.inspect}"

puts "word1 == word2: #{word1 == word2}"
puts "word1 == word1: #{word1 == word1}"

puts "word1 < word2: #{word1 < word2}"
puts "word1 > word2: #{word1 > word2}"
puts "word1 <=> word2: #{word1 <=> word2}"

puts "word1.capitalize: #{word1.capitalize.inspect}"
puts "word1.downcase: #{word1.downcase.inspect}"
puts "word1.upcase: #{word1.upcase.inspect}"
puts "word1.reverse: #{word1.reverse.inspect}"

puts "Is '#{word1.text}' a punctuation? #{word1.punctuation?}"
puts "Is '#{word3.text}' a punctuation? #{word3.punctuation?}"
