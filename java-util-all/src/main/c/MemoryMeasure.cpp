#include "util_memory_MemoryMeasure.h"

jlong JNICALL Java_util_memory_MemoryMeasure_sizeof(jobject obj) {
  return (jlong) sizeof(obj);
}
