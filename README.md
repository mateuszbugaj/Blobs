# Procedurally generated blobs/bubbles

[More detailed explanation](https://mateuszbugaj.github.io/ProceduralBlobsGeneration/)

This method allows to generate globs or bubbles and make them behave in convincing way.
It is similar to Worley noise where we calculate each pixels distance to the closest of the specified random points.
I added on top of that restrictions of distance so particles can have specified size. Colors of pixels based not on distance
but on color of closest particle and forces acting on particles creating movement similar to one seen in soap bubbles.<br/>

![Blobs](https://media.giphy.com/media/ZYKgyrS5klSwXYyJJg/giphy.gif)
