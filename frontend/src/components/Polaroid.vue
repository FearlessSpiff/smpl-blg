<script>
import "@fontsource/oleo-script";

export default {
  data() {
    return {
      degree: (Math.random() < 0.5 ? -5 : 5) * Math.random(),
      dialog: false
    }
  },
  computed: {
    rotate() {
      return {transform: 'rotate(' + this.degree + 'deg)'}
    },
    isPortrait() {
      return (this.image.height / this.image.width).toFixed(1) >=  1;
    }
  },
  props: ['image']

}

</script>

<template>
  <v-dialog v-model="dialog" width="auto" height="auto">

    <template v-slot:activator="{ props }">
      <v-card class="polaroid ma-2" :style="rotate" v-bind="props">
        <v-img :min-width="360" class="ma-3" :src="'/api/v1/' + image.thumbUrl" :alt="image.name"/>
        <v-card-text class="handwriting">
          <h1 class="font-weight-thin">{{ image.name }}</h1>
          <h5 class="font-weight-thin">{{ image.dateTime }}</h5>
        </v-card-text>
      </v-card>
    </template>

    <v-card class="polaroid ma-2" >
      <v-img class="ma-3" :src="'/api/v1/' + image.url" :alt="image.name" :class="{ portrait: isPortrait, landscape: !isPortrait }" max-height="80vh" :max-width="image.width" contain/>
      <v-card-text class="handwriting">
        <h1 class="font-weight-thin">{{ image.name }}</h1>
        <h5 class="font-weight-thin">{{ image.dateTime }}</h5>
      </v-card-text>
    </v-card>

  </v-dialog>
</template>


<style scoped>

.polaroid {
  background-color: white;
  color: #121212;
  box-shadow: 0 3px 3px rgba(0, 0, 0, 0.2);
  font-family: "Oleo Script", serif;
}

.handwriting {
  text-align: right !important;
}

.portrait {
  width: 35vw;
}

.landscape {
  width: 80vw;
}

</style>
