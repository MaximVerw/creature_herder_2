package io.github.creature.herder.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RenderableObject {
  public Renderable renderable;
  public boolean isDisposed = false;

  public void update(float delta) {}

  public void dispose() {
    isDisposed = true;
  }
}
