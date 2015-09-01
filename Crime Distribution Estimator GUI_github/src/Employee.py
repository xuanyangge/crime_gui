# Jython source file
from jythontest import EmployeeType

class Employee(EmployeeType):
   def __init__(self):
      self.first = "John"
      self.last  = "White"
      self.id = "myempid"

   def getEmployeeFirst(self,p):
      return p

   def getEmployeeLast(self):
      return self.last

   def getEmployeeId(self):
      return self.id