#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <sys/mount.h>

int main()
{
   const char* src  = "none";
   const char* trgt = "/var/tmp";
   const char* type = "tmpfs";
   const unsigned long mntflags = 0;
   const char* opts = "mode=0700,uid=65534";   /* 65534 is the uid of nobody */

   int result = mount(src, trgt, type, mntflags, opts);

   if (result == 0)
   {
      printf("Mount created at %s...\n", trgt);
      printf("Press <return> to unmount the volume: ");
      getchar();

      umount(trgt);
   }
   else
   {
      printf("Error : Failed to mount %s\n"
             "Reason: %s [%d]\n",
             src, strerror(errno), errno);
      return -1;
   }

   return 0;
}
