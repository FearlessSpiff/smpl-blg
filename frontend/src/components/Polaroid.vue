<script>
import "@fontsource/oleo-script";

export default {
  data() {
    return {
      degree: (Math.random() < 0.5 ? -5 : 5) * Math.random()
    }
  },
  computed: {
    rotate() {
      return {transform: 'rotate(' + this.degree + 'deg)'}
    }
  },
  props: ['image']
}

</script>

<template>
  <v-card class="polaroid ma-2" variant="tonal" :style="rotate">
    <v-img :min-width="360" class="ma-3" :src="'/api/v1/' + image.thumbUrl" :alt="image.name"/>
    <v-card-text class="handwriting">
      <h1 class="font-weight-thin">{{ image.name }}</h1>
      <h5 class="font-weight-thin">{{ image.dateTime }}</h5>
    </v-card-text>
  </v-card>
</template>


<style scoped>

.polaroid {
  background-color: white;
  color: #121212;
  box-shadow: 0 3px 3px rgba(0,0,0,0.2);
  font-family: "Oleo Script",serif;
}

.handwriting {
  text-align: right !important;
}

</style>
