// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

//
//  IOSClassTest.m
//  JreEmulation
//
//  Created by Tom Ball on 6/19/13.
//
//

#import "JreEmulation.h"

#import "IOSClass.h"
#import "IOSPrimitiveArray.h"
#import "IOSObjectArray.h"
#import "java/lang/Double.h"
#import "java/util/Arrays.h"
#import "java/lang/ref/WeakReference.h"

#import <XCTest/XCTest.h>

// Unit tests for IOSClass.
@interface IOSClassTest : XCTestCase
@end


@implementation IOSClassTest

- (void)testCheckDoubleParameterNaming {
  IOSClass *arraysClass = [JavaUtilArrays getClass];
  IOSObjectArray *argTypes =
      [IOSObjectArray arrayWithObjects:
          (id[]){ [IOSDoubleArray iosClass], JavaLangDouble_get_TYPE_() }
                                 count:2
                                  type:[IOSClass getClass]];
  id method = [arraysClass getMethod:@"binarySearch" parameterTypes:argTypes];
  XCTAssertNotNil(method, @"Arrays.binarySearch(double[], double) not found");
}

- (void)testHash {
  IOSClass *arrayClass = [IOSClass classWithClass:[IOSArray class]];
  XCTAssertTrue([arrayClass hash] == [arrayClass hash], @"IOSClasses should have same hash if they are the same object");
  XCTAssertEqual([arrayClass hash], [[IOSClass classWithClass:[IOSArray class]] hash], @"Hash should be same for same class");
  XCTAssertFalse([arrayClass hash] == [[IOSClass classWithClass:[NSObject class]] hash], @"IOSClasses should not have same hash if the classes are not referent subclasses of each other");

  id array = [[[IOSArray alloc] init] autorelease];
  [[[JavaLangRefWeakReference alloc] initWithId:array] autorelease];
  XCTAssertTrue([[array getClass] hash] == [arrayClass hash], @"Hash of referent subclass should be same as original class");
}

- (void)testIsEqual {
  IOSClass *exampleClass = [IOSClass classWithClass:[IOSArray class]];
  XCTAssertTrue([exampleClass isEqual:exampleClass], @"IOSClasses should be equal if they are the same object");
  XCTAssertTrue([exampleClass isEqual:[IOSClass classWithClass:[NSObject class]]], @"IOSClasses should be equal if they are objects of the same class");
  
  id object = [[[NSObject alloc] init] autorelease];
  [[[JavaLangRefWeakReference alloc] initWithId:object] autorelease];
  XCTAssertTrue([[object getClass] isEqual:exampleClass], @"IOSClasses should be equal if one is a referent subclass of the other");
}

@end
